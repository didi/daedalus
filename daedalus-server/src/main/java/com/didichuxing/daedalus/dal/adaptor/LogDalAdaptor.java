package com.didichuxing.daedalus.dal.adaptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.daedalus.common.dto.log.Log;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.dal.LogDal;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.handler.LogConverter;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import com.didichuxing.daedalus.util.OkHttpUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : jiangxinyu
 * @date : 2020/6/24
 */
@Repository
@Slf4j
public class LogDalAdaptor {
    @Value("${daedalus.online.url}")
    private String daedalusUrl;

    @Autowired
    private LogDal logDal;

    public LogEntity insert(LogEntity logEntity) {
        log.info("存储日志:pipelineId {}", logEntity.getPipelineId());
        if (EnvUtil.isOffline()) {
            Log logDto = LogConverter.entityToDto(logEntity);
            RequestBody requestBody = OkHttpUtil.buildRequestBody(BodyType.JSON, JSON.toJSONString(logDto), null);
            Request request = OkHttpUtil.buildPostRequest(daedalusUrl + "/log/insert", null, requestBody);
            try (Response response = OkHttpUtil.call(request, 10000)) {
                String result = response.body().string();
                log.info("存储日志结果:{}", result);
                com.didichuxing.daedalus.common.Response<String> logResponse = JSON.parseObject(result, new TypeReference<com.didichuxing.daedalus.common.Response<String>>() {
                });
                logEntity.setId(logResponse.getData());
            } catch (Exception e) {
                log.error("pipeline:{} 日志存储失败！", logEntity.getPipelineId(), e);
            }
            return logEntity;
        } else {
            return logDal.insert(logEntity);
        }
    }

    public LogEntity findById(String logId) {
        Optional<LogEntity> logEntity;
        if (EnvUtil.isOffline()) {
            logEntity = Optional.ofNullable(LogConverter.dtoToEntity(Context.getRequest().getLog()));
        } else {
            logEntity = logDal.findById(logId);
        }
        return logEntity.orElseThrow(() -> new ExecuteException("运行记录不存在！"));

    }
}
