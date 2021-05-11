package com.didichuxing.daedalus.service.dispatcher;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@Component
@Slf4j
public class NormalDispatchExecutor extends AbstractDispatcherExecutor {
    @Autowired
    private PipelineDal pipelineDal;


    @Override
    protected PipelineEntity getPipeline(ExecuteRequest request) {
        return pipelineDal.queryById(request.getPipelineId());
    }

    @Override
    protected ExecuteRequest buildRequest(ExecuteRequest request, PipelineEntity pipeline, ClusterEnum cluster) {

        if (cluster == ClusterEnum.OFFLINE) {
            return buildOfflineRequest(request, pipeline);
        }
        return request;


    }


}
