package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.InputTypeEnum;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.variables.InputVarEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@Component
public class InputVarDigger implements Digger {
    @Override
    public boolean condition(PipelineContext pipelineContext) {
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);
        return executeRequest.getInputs() != null && !executeRequest.getInputs().isEmpty();
    }

    @Override
    public void dig(PipelineContext pipelineContext) {
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);

        Map<String, String> inputs = executeRequest.getInputs();
        boolean hasInputVars = pipelineEntity.getVariable() != null && CollectionUtils.isNotEmpty(pipelineEntity.getVariable().getInputVars());


        if (hasInputVars) {
            //时间格式处理

            List<InputVarEntity> inputVars = pipelineEntity.getVariable().getInputVars();
            inputVars.stream()
                    .filter(inputVar -> inputVar.getInputType().equals(InputTypeEnum.DATE_TIME_PICKER))
                    .filter(inputVar -> inputVar.getDateFormat().isDateTime())
                    .forEach(inputVar -> {
                        String originDateTime = inputs.get(inputVar.getName());
                        inputs.put(inputVar.getName(), inputVar.getDateFormat().getConverter().apply(originDateTime));
                    });

            //输入变量额外变量放入变量池

            inputVars.stream()
                    .filter(inputVarEntity -> inputVarEntity.getInputType() == InputTypeEnum.RADIO || inputVarEntity.getInputType() == InputTypeEnum.SELECT)
                    .filter(inputVarEntity -> inputs.containsKey(inputVarEntity.getName()))
                    .forEach(inputVarEntity -> inputVarEntity.getOptions().stream()
                            .filter(option -> option.getValue().equals(inputs.get(inputVarEntity.getName())))
                            .findAny().ifPresent(optionEntity -> {
                                if (CollectionUtils.isNotEmpty(optionEntity.getExtraVars())) {
                                    for (Pair<String, String> extraVar : optionEntity.getExtraVars()) {
                                        pipelineContext.getVars().put(extraVar.getName(), extraVar.getValue());
                                    }
                                }
                            }));

        }

        pipelineContext.getVars().putAll(inputs);

    }
}
