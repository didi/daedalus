package com.didichuxing.daedalus.service.executor;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.enums.ExecuteStatusEnum;
import com.didichuxing.daedalus.common.enums.ExtractLocEnum;
import com.didichuxing.daedalus.common.enums.LogicMatcher;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.entity.log.StepLogEntity;
import com.didichuxing.daedalus.entity.log.StepResponseEntity;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.entity.step.additional.AssertEntity;
import com.didichuxing.daedalus.entity.step.additional.ConditionEntity;
import com.didichuxing.daedalus.entity.step.variables.ExtractVarEntity;
import com.didichuxing.daedalus.pojo.AssertException;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.VariableProcessor;
import com.didichuxing.daedalus.util.*;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.validation.constraints.NotNull;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/3/25
 */
@Slf4j
public abstract class Executor<T extends BaseStepEntity> {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String SCRIPT_CONTEXT = "context";
    private static final String SET_COOKIE = "Set-Cookie";

    @Autowired
    private VariableProcessor variableProcessor;

    protected void pre(PipelineContext pipelineContext, T step) {
        execStepScript(pipelineContext, step, step.getPreStepScript());

        variableProcessor.process(step, pipelineContext.getVars());


        //延迟执行
        if (step.getDelay() != null) {
            appendLog(pipelineContext, step, "延迟执行" + step.getDelay() + "ms...");
            ThreadUtil.sleep(step.getDelay());
        }
    }


    /**
     * 执行
     *
     * @param pipelineContext
     * @param step
     */
    protected abstract void exec(PipelineContext pipelineContext, T step);


    public ExecuteStatusEnum execute(PipelineContext pipelineContext, T step) {
        try {
            if (!condition(pipelineContext, step)) {
                return ExecuteStatusEnum.MISMATCH;
            }
            this.pre(pipelineContext, step);
            this.exec(pipelineContext, step);
            this.post(pipelineContext, step);
        } catch (Throwable e) {
            return afterThrow(e, pipelineContext, step);
        } finally {
            StepLogEntity stepLog = getStepLog(pipelineContext, step);
            StepResponseEntity stepResponse = stepLog.getStepResponse();
            //最终输出 todo 哪个是最终输出
//        if (step.getOutput() != null) {
            if (stepResponse != null) {
                pipelineContext.put(String.class, stepResponse.getResult());
                pipelineContext.get(LogEntity.class).setResult(stepResponse.getResult());
            }

//        }
        }
        return ExecuteStatusEnum.SUCCESS;
    }

    protected ExecuteStatusEnum afterThrow(Throwable throwable, PipelineContext pipelineContext, T step) {
        pipelineContext.put(Throwable.class, throwable);

        log.error("step:{} 运行出错！", step.getName(), throwable);
        if (throwable instanceof AssertException) {
            createResponse(pipelineContext, step, throwable.getMessage());
            pipelineContext.put(String.class, throwable.getMessage());
            appendLog(pipelineContext, step, throwable.getMessage());
            return ExecuteStatusEnum.ASSERT_FAILED;
        } else if (throwable instanceof ExecuteException) {
            createResponse(pipelineContext, step, throwable.getMessage());
            pipelineContext.put(String.class, throwable.getMessage());
            appendLog(pipelineContext, step, throwable.getMessage());
        } else {
            String msg = "Step运行失败！" + throwable.getMessage();
            createResponse(pipelineContext, step, msg);
            appendLog(pipelineContext, step, msg);
        }
        return ExecuteStatusEnum.FAILED;

    }

    protected void post(PipelineContext pipelineContext, T step) {

        //变量提取
        extractVars(pipelineContext, step);

        //assert
        assertStep(pipelineContext, step);

        //脚本
        execStepScript(pipelineContext, step, step.getPostStepScript());

    }

    /**
     * assert step whether failed
     *
     * @param pipelineContext
     * @param step
     */
    private void assertStep(PipelineContext pipelineContext, T step) {
        List<AssertEntity> asserts = step.getAsserts();
        if (CollectionUtils.isEmpty(asserts)) {
            return;
        }
        StepLogEntity stepLog = getStepLog(pipelineContext, step);
        StepResponseEntity stepResponse = stepLog.getStepResponse();
        String result = stepResponse.getResult();

        for (AssertEntity oneAssert : asserts) {
            LogicMatcher logicMatcher = oneAssert.getLogicMatcher();
            String expectValue = oneAssert.getExpectValue();
            String path = oneAssert.getPath();
            String varWithBrace = String.valueOf(oneAssert.getVar());//这里的变量未进行替换
            String realValue;
            if (StringUtils.isNotBlank(path)) {
                realValue = JsonUtil.getPath(result, path);
            } else {
                realValue = String.valueOf(pipelineContext.getVars().get(RegexUtil.getVar(varWithBrace)));
            }
            boolean matches = logicMatcher.getMatcher().apply(expectValue).matches(realValue);
            if (!matches) {
                String message = String.format("运行结果与预期不符！预期结果:%s,实际结果:%s,操作类型:%s", expectValue, realValue, logicMatcher.getDesc());
                appendLog(pipelineContext, step, message);
                throw new AssertException(message);
            }
        }
    }

    private void extractVars(PipelineContext pipelineContext, T step) {
        StepLogEntity stepLog = getStepLog(pipelineContext, step);
        StepResponseEntity stepResponse = stepLog.getStepResponse();
        List<ExtractVarEntity> extractVars = step.getExtractVars();

        if (stepResponse == null || CollectionUtils.isEmpty(extractVars)) {
            log.info("step response为空，不提取变量, step:{}, stepResponse:{}, extractVars:{}", step.getName(), stepResponse, extractVars);
            return;
        }


        extractVars.stream()
                .filter(ExtractVarEntity::valid)
                .forEach(extractVar -> {
                    String result = stepResponse.getResult();

                    String path = extractVar.getPath();
                    String name = extractVar.getName();

                    String varVal = null;

                    if (StringUtils.isBlank(path)) {
                        varVal = result;
                        pipelineContext.getVars().put(name, result);
                    } else {
                        ExtractLocEnum location = extractVar.getLocation();
                        Map<String, List<String>> headers = stepResponse.getHeaders();
                        if (location == ExtractLocEnum.RESULT) {
                            if (StringUtils.isBlank(result)) {
                                appendLog(pipelineContext, step, "Step结果为空，不进行变量提取");
                                return;
                            }
                            appendLog(pipelineContext, step, "从Result提取变量，Path为:" + path);
                            varVal = JsonUtil.getPath(result, path);
                            pipelineContext.getVars().put(name, varVal);

                        } else if (location == ExtractLocEnum.HTTP_HEADER && headers != null) {
                            appendLog(pipelineContext, step, "从Headers提取变量，Headers为:" + JSON.toJSONString(headers));
                            List<String> targetHeaders = headers.getOrDefault(path, Collections.emptyList());
                            if (SET_COOKIE.equalsIgnoreCase(path)) {
                                //set cookie 特殊处理
                                varVal = targetHeaders.stream().map(setCookie -> setCookie.split(";")[0]).collect(Collectors.joining(";"));
                            } else {
                                varVal = String.join(";", targetHeaders);
                            }
                            pipelineContext.getVars().put(name, varVal);
                        } else if (location == ExtractLocEnum.ATTACHMENT && stepResponse.getAttachments() != null) {
                            appendLog(pipelineContext, step, "从Attachment提取变量，Attachment为:" + JSON.toJSONString(stepResponse.getAttachments()));
                            varVal = stepResponse.getAttachments().get(path);
                            pipelineContext.getVars().put(name, varVal);
                        }

                        appendLog(pipelineContext, step, "变量名:" + name + "," + "变量值:" + varVal);
                    }
                    if (StringUtils.isBlank(varVal)) {
                        throw new ExecuteException(step.getName() + "提取到的变量值:" + extractVar.getName() + "为空！");
                    }
                });


    }

    /**
     * 当前step追加log
     *
     * @param context
     * @param step
     * @param log
     */
    public void appendLog(PipelineContext context, T step, String log) {
        StepLogEntity stepLog = getStepLog(context, step);
        String dateLog = "[" + LocalDateTime.now().format(DATE_TIME_FORMATTER) + "]  " + StringUtils.truncate(log, 20000);
        List<String> logs = stepLog.getLogs();
        if (logs == null) {
            logs = Lists.newArrayList(dateLog);
            stepLog.setLogs(logs);
        } else {
            logs.add(dateLog);
        }

    }


    protected StepLogEntity getStepLog(@NotNull PipelineContext pipelineContext, T step) {
        LogEntity log = pipelineContext.get(LogEntity.class);
        List<StepLogEntity> stepLogs = log.getStepLogs();
        synchronized (pipelineContext) {
            return stepLogs.stream()
                    .filter(sl -> step.getId().equals(sl.getStepId()))
                    .findFirst()
                    .orElseGet(() -> {
                        StepLogEntity stepLogEntity = new StepLogEntity(step.getId(), step.getName());
                        stepLogs.add(stepLogEntity);
                        return stepLogEntity;
                    });
        }
    }

    /**
     * 是否执行
     *
     * @param pipelineContext
     * @param step
     */
    private boolean condition(PipelineContext pipelineContext, T step) {
        if (step.getStepType() != getStepType()) {
            return false;
        }
        //变量替换 因为提取变量 所以需要在这里替换
        //只替换condition
        variableProcessor.process(step.getCondition(), pipelineContext.getVars());

        ConditionEntity condition = step.getCondition();
        boolean condRet = RuleUtil.condition(condition, pipelineContext.getVars(), log -> appendLog(pipelineContext, step, log));
        if (!condRet) {
            appendLog(pipelineContext, step, "不满足条件，Step不执行");
        }
        return condRet;
    }


    protected StepResponseEntity createResponse(PipelineContext pipelineContext, T step, Object result) {
        StepResponseEntity response = new StepResponseEntity();
        if (result == null) {
            response.setResult(null);
        } else if (result instanceof String || ClassUtils.isPrimitiveOrWrapper(result.getClass())) {
            response.setResult(String.valueOf(result));
        } else {
            response.setResult(JSON.toJSONString(result));
        }

        StepLogEntity stepLog = getStepLog(pipelineContext, step);
        stepLog.setStepResponse(response);
        return response;
    }

    /**
     * step type
     *
     * @return
     */
    public abstract StepTypeEnum getStepType();


    private void execStepScript(PipelineContext pipelineContext, T step, String script) {
        if (StringUtils.isBlank(script)) {
            return;
        }

        ScriptEngine engine = getScriptEngine(pipelineContext, step);

        try {
            Object eval = engine.eval(script);
            if (eval != null) {
                appendLog(pipelineContext, step, "前/后置脚本运行结果:" + JSON.toJSONString(eval));
            }
        } catch (Exception e) {
            appendLog(pipelineContext, step, "前/后置脚本运行失败.Error:" + e.getMessage());
            log.error("step:{} pre/post 脚本运行失败！", step.getName(), e);
            throw new ExecuteException(step.getName() + "Pre/Post 脚本运行失败！" + e.getMessage());
        }
    }

    private ScriptEngine getScriptEngine(PipelineContext pipelineContext, T step) {
        Map<String, String> vars = pipelineContext.getVars();
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        Bindings bindings = engine.createBindings();
        bindings.put(SCRIPT_CONTEXT, ImmutableMap.of("vars", vars));
        engine.setBindings(bindings, ScriptContext.ENGINE_SCOPE);
        StringWriter writer = new ListenerStringWriter(s -> appendLog(pipelineContext, step, s));
        engine.getContext().setWriter(writer);
        return engine;
    }
}
