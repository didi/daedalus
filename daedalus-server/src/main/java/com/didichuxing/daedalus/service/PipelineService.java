package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.aop.TimeLog;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.Validation;
import java.util.Set;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Service
@Slf4j
public class PipelineService {
    @Autowired
    private MiningEngine miningEngine;
    @Autowired
    private FlowEngine flowEngine;

    /**
     * 运行入口
     * @param request
     * @return
     */
    @TimeLog
    public ExecuteResult execute(@Validated ExecuteRequest request) {
        log.info("开始执行流水线, id:{}, env:{}, inputs:{} ", request.getPipelineId(), request.getEnv(), request.getInputs());
        Validator.valid(request);

        PipelineContext pipelineContext = PipelineContext.get(request.getPipelineId());

        pipelineContext.put(ExecuteRequest.class, request);

        miningEngine.dig(pipelineContext);

        flowEngine.fire(pipelineContext);


        log.info("流水线执行完成 id:{}", request.getPipelineId());

        return getExecuteResult(pipelineContext);

    }


    private ExecuteResult getExecuteResult(PipelineContext pipelineContext) {
        ExecuteResult executeResult = new ExecuteResult();
        executeResult.setResult(pipelineContext.get(String.class));
        executeResult.setLogId(pipelineContext.get(LogEntity.class).getId());
        Throwable throwable = pipelineContext.get(Throwable.class);
        executeResult.setSuccess(throwable == null);
        executeResult.setExceptionMsg(throwable == null ? null : throwable.getMessage());
        return executeResult;
    }



}
