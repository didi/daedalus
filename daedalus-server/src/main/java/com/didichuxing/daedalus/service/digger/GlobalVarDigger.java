package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.variables.GlobalVarEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Component
public class GlobalVarDigger implements Digger {

    @Override
    public boolean condition(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        return pipelineEntity.getVariable() != null && CollectionUtils.isNotEmpty(pipelineEntity.getVariable().getGlobalVars());
    }

    @Override
    public void dig(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        List<GlobalVarEntity> globalVars = pipelineEntity.getVariable().getGlobalVars();

        Map<String, String> vars = pipelineContext.getVars();
        globalVars.forEach(globalVarEntity -> vars.put(globalVarEntity.getName(), globalVarEntity.getValue()));
    }
}
