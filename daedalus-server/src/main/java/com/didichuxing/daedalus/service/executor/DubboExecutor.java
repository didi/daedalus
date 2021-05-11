package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.dto.step.additional.DubboParam;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.log.StepResponseEntity;
import com.didichuxing.daedalus.entity.step.DubboStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.InstanceCenter;
import com.didichuxing.daedalus.util.DubboUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class DubboExecutor extends Executor<DubboStepEntity> {

    @Autowired
    private InstanceCenter instanceCenter;

    @Override
    protected void exec(PipelineContext pipelineContext, DubboStepEntity step) {

        log(pipelineContext, step);


        Object result;
        try {
            switch (step.getDubboType()) {
                case DIRECT:
                    String dubboUrl = step.getIp() + ":" + step.getPort();
                    result = DubboUtils.invokeGeneric(step.getClassName(), step.getMethod(), null, step.getGroup(), step.getVersion(), dubboUrl, step.getParams(), step.getAttachments());
                    break;
                case REGISTER:
                    List<RegistryConfig> registry = instanceCenter.getRegistry(step.getRegister());
                    result = DubboUtils.invokeGeneric(step.getClassName(), step.getMethod(), registry, step.getGroup(), step.getVersion(), null, step.getParams(), step.getAttachments());
                    break;
                default:
                    log.warn("dubbo 类型:{}不支持", step.getDubboType());
                    return;
            }
            Map<String, String> attachments = RpcContext.getContext().getAttachments().entrySet().stream()
                    .collect(Collectors.toMap(entry -> entry.getKey().replace(".", "_"), Map.Entry::getValue));
            StepResponseEntity response = createResponse(pipelineContext, step, result);
            response.setAttachments(attachments);
        } catch (Exception e) {
            log.error("Dubbo调用失败！", e);
            if (e instanceof IllegalStateException && e.getMessage().contains("No provider available")) {
                throw new ExecuteException("Dubbo调用失败！请检查Provider、Group、Version、方法名等参数！");
            }
            throw new ExecuteException("Dubbo调用失败！" + e.getMessage());
        } finally {
            RpcContext.removeContext();
            RpcContext.removeServerContext();
        }


        log.info("step:{} dubbo:{}请求结果:{}", step.getId(), step.getClassName(), result);

    }

    private void log(PipelineContext pipelineContext, DubboStepEntity step) {
        appendLog(pipelineContext, step, "Dubbo调用方式:" + step.getDubboType());
        appendLog(pipelineContext, step, "接口:" + step.getClassName());
        appendLog(pipelineContext, step, "方法:" + step.getMethod());
        if (StringUtils.isNotBlank(step.getGroup())) {
            appendLog(pipelineContext, step, "Group:" + step.getGroup());
        }
        if (StringUtils.isNotBlank(step.getVersion())) {
            appendLog(pipelineContext, step, "Version:" + step.getVersion());
        }
        if (CollectionUtils.isNotEmpty(step.getParams())) {
            List<DubboParam<String, String>> params = step.getParams();
            for (int i = 1; i <= params.size(); i++) {
                DubboParam<String, String> param = params.get(i - 1);
                appendLog(pipelineContext, step, "第" + i + "个参数类型:" + param.getType());
                appendLog(pipelineContext, step, "第" + i + "个参数值:" + param.getValue());
            }
        }
    }


    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.DUBBO;
    }
}
