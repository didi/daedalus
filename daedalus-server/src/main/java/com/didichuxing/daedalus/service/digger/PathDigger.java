package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.entity.pipeline.EdgeEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 转换为执行链表
 *
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Component
@Slf4j
public class PathDigger implements Digger {
    @Override
    public boolean condition(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        return pipelineEntity.getFlow() != null && pipelineEntity.getFlow().getSteps() != null && pipelineEntity.getFlow().getEdges() != null;
    }

    @Override
    public void dig(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        List<BaseStepEntity> steps = pipelineEntity.getFlow().getSteps();
        List<EdgeEntity> edges = pipelineEntity.getFlow().getEdges();
        BaseStepEntity firstStep = steps.get(0);

        List<List<BaseStepEntity>> paths = new ArrayList<>();
        findPath(firstStep, steps, edges, new LinkedList<>(), paths);

        log.info("pipelineId:{},path:{}", pipelineContext.getPipelineId(), toPathLog(paths));
        pipelineContext.setPaths(paths);

    }


    /**
     * 找到下面的 step的id
     *
     * @param sourceStepId
     * @param edges
     * @return
     */
    private List<String> findNext(String sourceStepId, List<EdgeEntity> edges) {
        if (CollectionUtils.isEmpty(edges)) {
            return null;
        }
        return edges.stream()
                .filter(edge -> sourceStepId.equals(edge.getSource()))
                .map(EdgeEntity::getTarget)
                .collect(Collectors.toList());
    }

    private List<BaseStepEntity> findNext(String sourceStepId, List<BaseStepEntity> steps, List<EdgeEntity> edges) {
        return edges.stream()
                .filter(edge -> sourceStepId.equals(edge.getSource()))
                .map(edge -> steps.stream().filter(step -> step.getId().equals(edge.getTarget())).findFirst().get())
                .collect(Collectors.toList());
    }


    private boolean isLeaf(BaseStepEntity step, List<EdgeEntity> edges) {
        return CollectionUtils.isEmpty(findNext(step.getId(), edges));
    }


    public void findPath(BaseStepEntity root, List<BaseStepEntity> steps, List<EdgeEntity> edges, List<BaseStepEntity> path, List<List<BaseStepEntity>> finalPaths) {
        if (isLeaf(root, edges)) {
            //一条路径完成
            ArrayList<BaseStepEntity> finalPath = new ArrayList<>(path);
            finalPath.add(root);
            finalPaths.add(finalPath);
        } else {
            //非叶子节点
            path.add(root);
            List<BaseStepEntity> childs = findNext(root.getId(), steps, edges);
            for (BaseStepEntity child : childs) {
                ArrayList<BaseStepEntity> newPath = new ArrayList<>(path);
                findPath(child, steps, edges, newPath, finalPaths);
            }
        }
    }

    String toPathLog(List<List<BaseStepEntity>> paths) {
        StringBuilder sb = new StringBuilder();
        for (List<BaseStepEntity> path : paths) {
            String oneStringPath = path.stream().map(BaseStepEntity::getName).collect(Collectors.joining("->"));
            sb.append(oneStringPath).append(";");
        }
        return sb.toString();
    }


}
