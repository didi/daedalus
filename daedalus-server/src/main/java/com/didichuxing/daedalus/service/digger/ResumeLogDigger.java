package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.enums.StepStatusEnum;
import com.didichuxing.daedalus.dal.adaptor.LogDalAdaptor;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.didichuxing.daedalus.common.enums.ExecTypeEnum.RESUME;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@Component
public class ResumeLogDigger implements LogDigger {
    @Autowired
    private LogDalAdaptor logDal;

    @Override
    public boolean condition(PipelineContext pipelineContext) {
        return isType(pipelineContext, RESUME);
    }

    @Override
    public LogEntity getLog(PipelineContext pipelineContext) {

        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);

        //使用历史log
        LogEntity logEntity = logDal.findById(executeRequest.getResumeLogId());
        //删除运行失败的运行日志
        Optional.of(logEntity.getStepLogs())
                .ifPresent(stepLogs ->
                        stepLogs.removeIf(stepLog ->
                                stepLog.getStepStatus() == StepStatusEnum.RUN_FAILED));

        logEntity.setExecType(RESUME);

        return logEntity;
    }
}
