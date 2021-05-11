package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.aop.TimeLog;
import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.service.dispatcher.DebugDispatchExecutor;
import com.didichuxing.daedalus.service.dispatcher.DispatchExecutor;
import com.didichuxing.daedalus.service.dispatcher.NormalDispatchExecutor;
import com.didichuxing.daedalus.service.dispatcher.ResumeDispatchExecutor;
import com.didichuxing.daedalus.util.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/6/24
 */
@Service
@Slf4j
public class DispatchService {

    @Autowired
    private NormalDispatchExecutor normalExecDispatcher;
    @Autowired
    private DebugDispatchExecutor debugExecDispatcher;
    @Autowired
    private ResumeDispatchExecutor resumeExecDispatcher;


    private final Map<ExecTypeEnum, DispatchExecutor> dispatchExecutorMap = new HashMap<>();

    @PostConstruct
    private void init() {
        dispatchExecutorMap.put(ExecTypeEnum.NORMAL, normalExecDispatcher);
        dispatchExecutorMap.put(ExecTypeEnum.RESUME, resumeExecDispatcher);
        dispatchExecutorMap.put(ExecTypeEnum.DEBUG, debugExecDispatcher);
    }


    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_USED}, allEntries = true),
    })
    @TimeLog
    public ExecuteResult doDispatch(ExecuteRequest request) {
        DispatchExecutor dispatchExecutor = dispatchExecutorMap.get(request.getExecType());
        Validator.notNull(dispatchExecutor, ErrorCode.UNKNOWN_EXEC_TYPE);
        return dispatchExecutor.doDispatch(request);
    }


}
