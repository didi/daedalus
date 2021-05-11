package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.variables.InputVarEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 运行限制
 *
 * @author : jiangxinyu
 * @date : 2020/5/9
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RestrictionDigger implements Digger {
    @Override
    public void dig(PipelineContext pipelineContext) {
        PipelineEntity pipeline = pipelineContext.get(PipelineEntity.class);
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);

        //检查steps
        if (pipeline.getFlow() == null || CollectionUtils.isEmpty(pipeline.getFlow().getSteps())) {
            throw new ExecuteException(ErrorCode.EMPTY_FLOW.getDesc());
        }

        //检查输入
        checkInputs(pipelineContext);

        //检查环境
        boolean envSupport = pipeline.isEnvSupport();
        if (envSupport && StringUtils.isBlank(executeRequest.getEnv())) {
            throw new ExecuteException("请选择运行环境！");
        }
    }

    /**
     * 检查用户输入
     *
     * @param pipelineContext
     */
    private void checkInputs(PipelineContext pipelineContext) {
        PipelineEntity pipeline = pipelineContext.get(PipelineEntity.class);
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);
        List<InputVarEntity> inputVars = pipeline.getVariable().getInputVars();

        if (CollectionUtils.isEmpty(inputVars)) {
            return;
        }
        Map<String, String> inputs = Optional.ofNullable(executeRequest.getInputs()).orElse(new HashMap<>());
        Set<String> inputKeys = inputs.keySet();

        Set<String> allRequiredInputNames = inputVars.stream()
                .filter(InputVarEntity::isRequired)
                .map(InputVarEntity::getName)
                .collect(Collectors.toSet());

        allRequiredInputNames.removeAll(inputKeys);


        //根据下拉选项值隐藏的字段不需填写
        Set<String> noNeedInputVars = inputVars.stream().filter(var -> allRequiredInputNames.contains(var.getName()))
                .filter(var -> StringUtils.isNotBlank(var.getDependencyInputName()) && CollectionUtils.isNotEmpty(var.getDependencyOptions()))
                .filter(var -> {
                    String dependencyInputName = var.getDependencyInputName();
                    List<String> dependencyOptions = var.getDependencyOptions();
                    return !dependencyOptions.contains(inputs.get(dependencyInputName));
                }).map(InputVarEntity::getName).collect(Collectors.toSet());
        allRequiredInputNames.removeAll(noNeedInputVars);

        if (CollectionUtils.isNotEmpty(allRequiredInputNames)) {
            throw new ExecuteException("请填写所有表单！" + String.join(",", allRequiredInputNames));
        }


    }
}
