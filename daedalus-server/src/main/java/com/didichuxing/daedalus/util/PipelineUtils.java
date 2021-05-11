package com.didichuxing.daedalus.util;

import com.alibaba.druid.sql.SQLUtils;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.pipeline.Edge;
import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
import com.didichuxing.daedalus.common.dto.step.*;
import com.didichuxing.daedalus.common.dto.step.variables.GlobalVar;
import com.didichuxing.daedalus.common.dto.step.variables.InputVar;
import com.didichuxing.daedalus.common.dto.step.variables.Option;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.ImportStepEntity;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.experimental.UtilityClass;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@UtilityClass
public class PipelineUtils {

    public static void valid(Pipeline pipeline, Function<List<String>, List<PipelineEntity>> selector) {
        validInputs(pipeline);
        validGlobal(pipeline);
        checkCycle(pipeline);
        checkNestedPipeline(pipeline, selector);
        sqlFormat(pipeline);
        remove(pipeline);
    }

    private static void remove(Pipeline pipeline) {
        if (pipeline.getVariable() != null && pipeline.getVariable().getInputVars() != null) {
            for (InputVar inputVar : pipeline.getVariable().getInputVars()) {
                if (CollectionUtils.isNotEmpty(inputVar.getOptions())) {
                    for (Option option : inputVar.getOptions()) {
                        removeEmpty(option.getExtraVars());
                    }
                }
            }
        }
        if (pipeline.getFlow() != null && pipeline.getFlow().getSteps() != null) {
            for (BaseStep step : pipeline.getFlow().getSteps()) {
                if (step instanceof HttpStep) {
                    HttpStep httpStep = (HttpStep) step;
                    removeEmpty(httpStep.getHeaders());
                    removeEmpty(httpStep.getCookies());
                    removeEmpty(httpStep.getFormData());
                    removeEmpty(httpStep.getUrlParams());
                } else if (step instanceof DubboStep) {
                    DubboStep dubboStep = (DubboStep) step;
                    removeEmpty(dubboStep.getAttachments());
                }
            }
        }
    }

    private static <K, V> void removeEmpty(List<Pair<K, V>> pairList) {
        if (CollectionUtils.isEmpty(pairList)) {
            return;
        }

        pairList.removeIf(pair -> !pair.valid());

    }


    private static void sqlFormat(Pipeline pipeline) {
        if (pipeline == null || pipeline.getFlow() == null || pipeline.getFlow().getSteps() == null) {
            return;
        }
        pipeline.getFlow().getSteps().stream()
                .filter(step -> step.getStepType().equals(StepTypeEnum.MYSQL))
                .forEach(step -> {
                    MysqlStep mysqlStep = (MysqlStep) step;
                    mysqlStep.setSql(SQLUtils.formatMySql(mysqlStep.getSql()));
                });
    }

    private static void validInputs(Pipeline pipeline) {
        if (pipeline.getVariable() == null || pipeline.getVariable().getInputVars() == null) {
            return;
        }
        List<InputVar> inputVars = pipeline.getVariable().getInputVars();
        if (CollectionUtils.isNotEmpty(inputVars)) {
            List<String> invarNames = inputVars.stream().map(InputVar::getName).collect(Collectors.toList());

            Set<String> duplicateNames = invarNames.stream()
                    .filter(name -> Collections.frequency(invarNames, name) > 1)
                    .collect(Collectors.toSet());
            if (duplicateNames.size() > 0) {
                throw new ExecuteException("用户输入中存在重复变量！" + String.join(",", duplicateNames));
            }
        }
    }

    private static void validGlobal(Pipeline pipeline) {
        if (pipeline.getVariable() == null || pipeline.getVariable().getGlobalVars() == null) {
            return;
        }
        List<GlobalVar> globalVars = pipeline.getVariable().getGlobalVars();
        if (CollectionUtils.isNotEmpty(globalVars)) {
            List<String> invarNames = globalVars.stream().map(GlobalVar::getName).collect(Collectors.toList());

            Set<String> duplicateNames = invarNames.stream()
                    .filter(name -> Collections.frequency(invarNames, name) > 1)
                    .collect(Collectors.toSet());
            if (duplicateNames.size() > 0) {
                throw new ExecuteException("全局变量中存在重复变量！" + String.join(",", duplicateNames));
            }
        }

    }

    /**
     * 检查流水线中是否有死循环和没有被指向的节点
     *
     * @param pipeline
     */
    private static void checkCycle(Pipeline pipeline) {
        if (pipeline.getFlow() == null) {
            return;
        }
        List<Edge> edges = pipeline.getFlow().getEdges();
        List<BaseStep> steps = pipeline.getFlow().getSteps();
        if (CollectionUtils.isEmpty(steps) || steps.size() < 2) {
            return;
        }

        for (int i = 1; i < steps.size(); i++) {
            BaseStep step = steps.get(i);
            checkCycle(step.getId(), step.getId(), steps.get(0).getId(), steps, edges);
        }


    }


    private void checkCycle(String currentStepId, String finalTargetId, String rootId, List<BaseStep> steps, List<Edge> edges) {
        Set<String> parentIds = findParentIds(currentStepId, edges);
        if (CollectionUtils.isEmpty(parentIds)) {
            //只有root step的父节点是空的
            if (!rootId.equals(currentStepId)) {
                steps.stream().filter(step -> step.getId().equals(currentStepId)).findFirst().ifPresent(step -> {
                    throw new BizException("Step名称：" + step.getName() + "没有父节点，请修改！");
                });
            }
            return;
        }
        if (parentIds.contains(finalTargetId)) {
            steps.stream().filter(step -> step.getId().equals(currentStepId)).findFirst().ifPresent(step -> {
                throw new BizException("Step名称：" + step.getName() + "发现有循环连接线，请修改后保存！");
            });
        }
        for (String parentId : parentIds) {
            checkCycle(parentId, finalTargetId, rootId, steps, edges);
        }

    }

    private Set<String> findParentIds(String stepId, List<Edge> edges) {
        return edges.stream()
                .filter(edge -> edge.getTarget().equals(stepId))
                .map(Edge::getSource)
                .collect(Collectors.toSet());

    }

    /**
     * 流水线引入只允许有一层
     *
     * @param pipeline
     * @param selector
     */
    private void checkNestedPipeline(Pipeline pipeline, Function<List<String>, List<PipelineEntity>> selector) {
        if (pipeline == null || pipeline.getFlow() == null || pipeline.getFlow().getSteps() == null) {
            return;
        }

        List<ImportStep> importSteps = pipeline.getFlow().getSteps().stream()
                .filter(step -> step instanceof ImportStep)
                .map(step -> (ImportStep) step)
                .collect(Collectors.toList());

        List<String> pipelineIds = importSteps
                .stream()
                .map(ImportStep::getPipelineId)
                .collect(Collectors.toList());

        if (org.apache.commons.collections.CollectionUtils.isEmpty(pipelineIds)) {
            return;
        }

        selector.apply(pipelineIds).stream()
                .filter(PipelineUtils::hasImportStep)
                .findAny()
                .ifPresent(pipelineEntity -> {
                    throw new BizException("引用的流水线中已经引用其他流水线，不允许循环引用！");
                });

    }

    private static boolean hasImportStep(PipelineEntity pipelineEntity) {
        if (pipelineEntity == null || pipelineEntity.getFlow() == null || pipelineEntity.getFlow().getSteps() == null) {
            return false;
        }
        return pipelineEntity.getFlow().getSteps().stream().anyMatch(step -> step instanceof ImportStepEntity);
    }
}
