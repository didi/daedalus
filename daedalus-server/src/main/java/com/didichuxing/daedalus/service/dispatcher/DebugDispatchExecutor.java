package com.didichuxing.daedalus.service.dispatcher;

import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.util.IdGenerator;
import com.didichuxing.daedalus.util.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@Component
public class DebugDispatchExecutor extends AbstractDispatcherExecutor {
    @Override
    protected PipelineEntity getPipeline(ExecuteRequest request) {
        Pipeline pipeline = request.getPipeline();
        Validator.assertTrue(pipeline != null && pipeline.getFlow() != null && CollectionUtils.isNotEmpty(pipeline.getFlow().getSteps()), "需要有Step才能debug！");

        //debug没有id 需手动set
        pipeline.setId("DEBUG-" + IdGenerator.id());
        return PipelineConverter.dtoToEntity(pipeline);
    }

    @Override
    protected ExecuteRequest buildRequest(ExecuteRequest request, PipelineEntity pipeline, ClusterEnum cluster) {
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setExecType(ExecTypeEnum.DEBUG);
        executeRequest.setPipelineId(pipeline.getId());
        executeRequest.setPipeline(request.getPipeline());
        executeRequest.setEnv(request.getEnv());
        executeRequest.setInputs(request.getInputs());
        if (cluster == ClusterEnum.OFFLINE) {
            return buildOfflineRequest(executeRequest, pipeline);
        }
        return executeRequest;
    }
}
