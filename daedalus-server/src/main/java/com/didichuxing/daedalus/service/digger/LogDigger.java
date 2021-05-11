package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
public interface LogDigger extends Digger {

    @Override
    default void dig(PipelineContext pipelineContext) {
        LogEntity logEntity = getLog(pipelineContext);
        completeLog(pipelineContext, logEntity);
        pipelineContext.put(LogEntity.class, logEntity);

    }

    LogEntity getLog(PipelineContext pipelineContext);

    default void completeLog(PipelineContext pipelineContext, LogEntity logEntity) {
        logEntity.setId(null);
        logEntity.setInputs(pipelineContext.get(ExecuteRequest.class).getInputs());
        logEntity.setUsername(Context.getUser().getUsername());
        logEntity.setUsernameCN(Context.getUser().getUsernameCN());
        logEntity.setCluster(EnvUtil.isOffline() ? ClusterEnum.OFFLINE : ClusterEnum.ONLINE);
    }

}
