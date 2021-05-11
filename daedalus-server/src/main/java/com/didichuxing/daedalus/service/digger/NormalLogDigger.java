package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import org.springframework.stereotype.Component;

import static com.didichuxing.daedalus.common.enums.ExecTypeEnum.DEBUG;
import static com.didichuxing.daedalus.common.enums.ExecTypeEnum.NORMAL;

/**
 * @author : jiangxinyu
 * @date : 2020/12/23
 */
@Component
public class NormalLogDigger implements LogDigger {


    @Override
    public boolean condition(PipelineContext pipelineContext) {
        return isType(pipelineContext, NORMAL) || isType(pipelineContext, DEBUG);
    }

    @Override
    public LogEntity getLog(PipelineContext pipelineContext) {
        String pipelineId = pipelineContext.getPipelineId();

        //新log
        LogEntity logEntity = new LogEntity();
        logEntity.setPipelineId(pipelineId);
        logEntity.setExecType(pipelineContext.get(ExecuteRequest.class).getExecType());

        return logEntity;


    }
}
