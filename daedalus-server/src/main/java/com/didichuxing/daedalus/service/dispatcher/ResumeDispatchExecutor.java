package com.didichuxing.daedalus.service.dispatcher;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.dal.LogDal;
import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.handler.LogConverter;
import com.didichuxing.daedalus.pojo.Constants;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@Component
@Slf4j
public class ResumeDispatchExecutor extends AbstractDispatcherExecutor {
    @Autowired
    private LogDal logDal;
    @Autowired
    private PipelineDal pipelineDal;

    @Override
    protected PipelineEntity getPipeline(ExecuteRequest request) {
        LogEntity logEntity = getLogEntity(request);
        Validator.assertTrue(logEntity.getCluster() != null, "历史运行记录不支持恢复运行！");

        String pipelineId = logEntity.getPipelineId();
        PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
        Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);
        return pipelineEntity;
    }

    @Override
    protected ExecuteRequest buildRequest(ExecuteRequest request, PipelineEntity pipeline, ClusterEnum cluster) {
        //从logid中构建request
        String resumeLogId = request.getResumeLogId();
        LogEntity logEntity = getLogEntity(request);
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setPipelineId(logEntity.getPipelineId());
        executeRequest.setInputs(logEntity.getRuntimeVars());
        executeRequest.setExecType(ExecTypeEnum.RESUME);
        executeRequest.setResumeLogId(resumeLogId);
        executeRequest.setEnv(logEntity.getRuntimeVars().get(Constants.ENV));

        if (cluster == ClusterEnum.OFFLINE) {
            //补充log信息
            executeRequest.setLog(LogConverter.entityToDto(logEntity));
            buildOfflineRequest(executeRequest, pipeline);
        }
        return executeRequest;
    }

    private LogEntity getLogEntity(ExecuteRequest request) {
        String logId = request.getResumeLogId();
        log.info("继续运行历史记录:{}", logId);
        return logDal.findById(logId).orElseThrow(() -> new ExecuteException("运行记录不存在！"));
    }

    @Override
    protected ClusterEnum determineCluster(ExecuteRequest request, PipelineEntity pipelineEntity) {
        LogEntity logEntity = getLogEntity(request);
        if (logEntity.getCluster() == null) {
            throw new ExecuteException("历史运行记录不支持失败重新运行！");
        }
        return logEntity.getCluster();
    }
}
