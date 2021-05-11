package com.didichuxing.daedalus.service.executor;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.GroovyStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.google.common.collect.ImmutableMap;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.MissingPropertyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class GroovyExecutor extends Executor<GroovyStepEntity> {
    private static final Pattern PATTERN = Pattern.compile("def\\s+(\\w)+\\s*=");

    @Override
    protected void exec(PipelineContext pipelineContext, GroovyStepEntity step) {
        if (StringUtils.isBlank(step.getScript())) {
            appendLog(pipelineContext, step, "[WARN]Groovy脚本为空，退出执行");
            return;
        }


        Binding binding = new Binding();
        shareVars(binding, pipelineContext.getVars());

        GroovyShell groovyShell = new GroovyShell(binding);
        Object result = null;
        try {
            result = groovyShell.evaluate(step.getScript());
            createResponse(pipelineContext, step, result);
            appendLog(pipelineContext, step, "当前变量：" + JSON.toJSONString(pipelineContext.getVars()));
        } catch (MissingPropertyException me) {
            log.error("Groovy执行失败！", me);
            appendLog(pipelineContext, step, "运行失败！脚本中使用的变量不存在！请检查脚本代码！");
            createResponse(pipelineContext, step, step.getName() + "运行失败！脚本中使用的变量不存在！请检查脚本代码！");
            throw new ExecuteException("Groovy脚本中使用的变量不存在！请检查脚本代码！");
        } catch (Throwable e) {
            log.error("pipelineId:{},step:{},groovy 脚本运行异常", pipelineContext.getPipelineId(), step.getName(), e);
            appendLog(pipelineContext, step, "运行失败！脚本运行抛出异常:" + e.getClass().getName() + ";ErrorMessage:" + e.getMessage());
            createResponse(pipelineContext, step, step.getName() + "运行失败！脚本运行抛出异常:" + e.getClass().getName() + ";ErrorMessage:" + e.getMessage());
            throw new ExecuteException("Groovy脚本运行出现异常，请查看日志！");
        }


    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.GROOVY;
    }

    public void shareVars(Binding binding, Map<String, String> vars) {
        if (vars.isEmpty()) {
            return;
        }
        //context.vars互通
        ImmutableMap<String, Map<String, String>> context = ImmutableMap.of("vars", vars);
        binding.setProperty("context", context);
        //直接变量互通
        vars.forEach((BiConsumer<String, Object>) binding::setProperty);
    }
}
