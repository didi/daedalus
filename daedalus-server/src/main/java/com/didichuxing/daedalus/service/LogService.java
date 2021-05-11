package com.didichuxing.daedalus.service;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.dto.log.Log;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.LogDal;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.handler.LogConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Service
@Slf4j
public class LogService {

    @Autowired
    private LogDal logDal;

    public PageResponse<List<Log>> list(String pipelineId, int page, int pageSize) {
        log.info("查询流水线:{} log", pipelineId);
        Page<LogEntity> logPage = logDal.queryByPipelineId(pipelineId, page, pageSize);
        List<Log> logs = logPage.getContent().stream().map(LogConverter::entityToDto).collect(Collectors.toList());
        return PageResponse.of(page, pageSize, logPage.getTotalPages(), logPage.getTotalElements(), logs);
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_LOG, key = "#logId")
    public Log logDetail(String logId) {
        Optional<LogEntity> optionalLogEntity = logDal.findById(logId);
        LogEntity logEntity = optionalLogEntity.orElse(null);
        return LogConverter.entityToDto(logEntity);
    }

    public String insertLog(Log logDto) {
        log.info("存储流水线日志:{}", JSON.toJSONString(logDto));
        LogEntity entity = LogConverter.dtoToEntity(logDto);
        return logDal.insert(entity).getId();
    }
}
