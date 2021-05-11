package com.didichuxing.daedalus.handler;

import com.didichuxing.daedalus.common.dto.pipeline.*;
import com.didichuxing.daedalus.common.dto.step.BaseStep;
import com.didichuxing.daedalus.common.dto.step.additional.Condition;
import com.didichuxing.daedalus.common.dto.step.additional.Rule;
import com.didichuxing.daedalus.common.dto.step.variables.*;
import com.didichuxing.daedalus.config.StepMapper;
import com.didichuxing.daedalus.entity.pipeline.EdgeEntity;
import com.didichuxing.daedalus.entity.pipeline.FlowEntity;
import com.didichuxing.daedalus.entity.pipeline.PermissionEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.entity.step.additional.ConditionEntity;
import com.didichuxing.daedalus.entity.step.additional.RuleEntity;
import com.didichuxing.daedalus.entity.step.variables.*;
import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.didichuxing.daedalus.handler.SimpleConverter.convert;
import static com.didichuxing.daedalus.handler.SimpleConverter.convertList;

/**
 * @author : jiangxinyu
 * @date : 2020/3/23
 */
@UtilityClass
public class PipelineConverter {

    public static PipelineEntity dtoToEntity(Pipeline pipeline) {
        if (pipeline == null) {
            return null;
        }

        PipelineEntity pipelineEntity = convert(pipeline, PipelineEntity.class);
        pipelineEntity.setPermission(convert(pipeline.getPermission(), PermissionEntity.class));
        pipelineEntity.setFlow(flowDtoToEntity(pipeline.getFlow()));
        pipelineEntity.setVariable(varDtoToEntity(pipeline.getVariable()));
        return pipelineEntity;
    }

    public static Pipeline entityToDto(PipelineEntity entity) {
        if (entity == null) {
            return null;
        }
        Pipeline pipeline = convert(entity, Pipeline.class);
        pipeline.setPermission(convert(entity.getPermission(), Permission.class));
        pipeline.setFlow(flowEntityToDto(entity.getFlow()));
        pipeline.setVariable(varEntityToDto(entity.getVariable()));
        return pipeline;
    }


    public static PipelineInfo entityToInfo(PipelineEntity entity) {
        PipelineInfo statistics = new PipelineInfo();
        BeanUtils.copyProperties(entity, statistics);
        return statistics;
    }


    private static FlowEntity flowDtoToEntity(Flow flow) {

        if (flow == null) {
            return null;
        }
        FlowEntity flowEntity = new FlowEntity();
        if (flow.getEdges() != null) {
            List<EdgeEntity> edges = SimpleConverter.convertList(flow.getEdges(), EdgeEntity.class);
            flowEntity.setEdges(edges);
        }
        if (flow.getSteps() != null) {
            List<BaseStepEntity> steps = flow.getSteps().stream().map(PipelineConverter::dtoToEntity).collect(Collectors.toList());
            flowEntity.setSteps(steps);
        }
        return flowEntity;
    }

    private static Flow flowEntityToDto(FlowEntity flowEntity) {
        if (flowEntity == null) {
            return null;
        }
        Flow flow = convert(flowEntity, Flow.class);
        if (flowEntity.getEdges() != null) {
            List<Edge> edges = SimpleConverter.convertList(flowEntity.getEdges(), Edge.class);
            flow.setEdges(edges);
        }
        if (flowEntity.getSteps() != null) {
            flow.setSteps(flowEntity.getSteps().stream().map(PipelineConverter::entityToDto).collect(Collectors.toList()));
        }
        return flow;
    }

    private static BaseStepEntity dtoToEntity(BaseStep step) {
        Class<? extends BaseStepEntity> entityClass = StepMapper.getEntityClass(step.getStepType());
        BaseStepEntity entity = convert(step, entityClass);

        entity.setCondition(conditionDtoToEntity(step.getCondition()));
        entity.setExtractVars(convertList(step.getExtractVars(), ExtractVarEntity.class));
        return entity;
    }

    private static BaseStep entityToDto(BaseStepEntity entity) {
        Class<? extends BaseStep> dtoClass = StepMapper.getDtoClass(entity.getStepType());
        BaseStep step = convert(entity, dtoClass);
        step.setCondition(conditionEntityToDto(entity.getCondition()));
        return step;
    }

    private static VariableEntity varDtoToEntity(Variable variable) {
        if (variable == null) {
            return null;
        }
        VariableEntity variableEntity = new VariableEntity();
        List<InputVarEntity> inputVars = variable.getInputVars().stream().map(var -> {
            InputVarEntity entity = convert(var, InputVarEntity.class);
            entity.setOptions(convertList(var.getOptions(), OptionEntity.class));
            Optional.ofNullable(var.getOptionRelations()).ifPresent(ors -> {
                entity.setOptionRelations(ors.stream().map(PipelineConverter::optionRelationDtoToEntity).collect(Collectors.toList()));
            });
            return entity;
        }).collect(Collectors.toList());


        variableEntity.setInputVars(inputVars);
        variableEntity.setGlobalVars(convertList(variable.getGlobalVars(), GlobalVarEntity.class));
        variableEntity.setDynamicVars(convertList(variable.getDynamicVars(), DynamicVarEntity.class));
        return variableEntity;
    }

    private static Variable varEntityToDto(VariableEntity variableEntity) {
        if (variableEntity == null) {
            return null;
        }
        Variable variable = new Variable();
        List<InputVar> inputVars = variableEntity.getInputVars().stream().map(var -> {
            InputVar inputVar = convert(var, InputVar.class);
            inputVar.setOptions(convertList(var.getOptions(), Option.class));
            Optional.ofNullable(var.getOptionRelations()).ifPresent(ors -> {
                inputVar.setOptionRelations(ors.stream().map(PipelineConverter::optionRelationEntityToDto).collect(Collectors.toList()));
            });
            return inputVar;
        }).collect(Collectors.toList());


        variable.setInputVars(inputVars);
        variable.setGlobalVars(convertList(variableEntity.getGlobalVars(), GlobalVar.class));
        variable.setDynamicVars(convertList(variableEntity.getDynamicVars(), DynamicVar.class));
        return variable;
    }

    private static Condition conditionEntityToDto(ConditionEntity conditionEntity) {
        if (conditionEntity == null) {
            return null;
        }
        Condition condition = new Condition();
        condition.setLogicType(conditionEntity.getLogicType());
        condition.setRules(convertList(conditionEntity.getRules(), Rule.class));
        return condition;
    }

    private static ConditionEntity conditionDtoToEntity(Condition condition) {
        if (condition == null) {
            return null;
        }
        ConditionEntity conditionEntity = new ConditionEntity();
        conditionEntity.setLogicType(condition.getLogicType());
        conditionEntity.setRules(convertList(condition.getRules(), RuleEntity.class));
        return conditionEntity;
    }

    private static OptionRelationEntity optionRelationDtoToEntity(OptionRelation optionRelation) {
        if (optionRelation == null) {
            return null;
        }
        OptionRelationEntity optionRelationEntity = new OptionRelationEntity();
        optionRelationEntity.setShowOnOptions((optionRelation.getShowOnOptions()));
        optionRelationEntity.setTargetOptions((optionRelation.getTargetOptions()));

        return optionRelationEntity;
    }

    private static OptionRelation optionRelationEntityToDto(OptionRelationEntity optionRelationEntity) {
        if (optionRelationEntity == null) {
            return null;
        }
        OptionRelation optionRelation = new OptionRelation();
//        optionRelation.setShowOnOptions(convertList(optionRelationEntity.getShowOnOptions(), Option.class));
//        optionRelation.setTargetOptions(convertList(optionRelationEntity.getTargetOptions(), Option.class));
        optionRelation.setShowOnOptions(optionRelationEntity.getShowOnOptions());
        optionRelation.setTargetOptions(optionRelationEntity.getTargetOptions());
        return optionRelation;
    }


}
