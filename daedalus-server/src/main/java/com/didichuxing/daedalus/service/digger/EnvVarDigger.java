package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.dal.adaptor.EnvDalAdaptor;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.pojo.Constants;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 环境变量挖掘
 *
 * @author : jiangxinyu
 * @date : 2020/4/20
 */
@Component
@Slf4j
public class EnvVarDigger implements Digger {

    @Autowired
    private EnvDalAdaptor envDal;

    @Override
    public boolean condition(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        return pipelineEntity.isEnvSupport();
    }


    @Override
    public void dig(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        String env = pipelineContext.get(ExecuteRequest.class).getEnv();
        pipelineContext.getVars().put(Constants.ENV, env);
        String envGroupId = pipelineEntity.getEnvGroupId();
        envDal.findById(envGroupId).ifPresent(envGroupEntity -> {

            List<LinkedHashMap<String, String>> envData = envGroupEntity.getData();
            Map<String, String> envVars = envData.stream().
                    collect(Collectors.toMap(vars -> vars.get("envVarName"), vars -> vars.get(env)));

            pipelineContext.getVars().putAll(envVars);
            log.info("环境:{} 环境变量:{}", env, envVars);
        });
    }
}
