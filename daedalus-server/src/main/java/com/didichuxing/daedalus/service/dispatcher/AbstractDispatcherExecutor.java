package com.didichuxing.daedalus.service.dispatcher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.dal.EnvDal;
import com.didichuxing.daedalus.dal.InstanceDal;
import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.*;
import com.didichuxing.daedalus.handler.EnvConverter;
import com.didichuxing.daedalus.handler.InstanceConverter;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.pojo.Constants;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.service.PipelineService;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.OkHttpUtil;
import com.didichuxing.daedalus.util.Validator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2021/1/20
 */
@Slf4j
public abstract class AbstractDispatcherExecutor implements DispatchExecutor {
    @Value("#{${cluster.config}}")
    private Map<ClusterEnum, String> clusterConfig;
    @Autowired
    private PipelineDal pipelineDal;
    @Autowired
    private InstanceDal instanceDal;
    @Autowired
    private EnvDal envDal;

    @Autowired
    private PipelineService pipelineService;


    @Override
    public final ExecuteResult doDispatch(ExecuteRequest request) {
        PipelineEntity pipeline = getPipeline(request);
        Validator.notNull(pipeline, ErrorCode.PIPELINE_NOT_FOUND);

        ClusterEnum cluster = determineCluster(request, pipeline);
        ExecuteRequest executeRequest = buildRequest(request, pipeline, cluster);

        return execute(executeRequest, cluster);

    }

    ExecuteResult execute(ExecuteRequest executeRequest, ClusterEnum cluster) {
        log.info("在集群:{}执行pipeline", cluster);
        String clusterUrl = clusterConfig.get(cluster);
        if (StringUtils.isNotBlank(clusterUrl)) {
            return executeOnCluster(executeRequest, clusterUrl);
        }
        return pipelineService.execute(executeRequest);

    }

    protected abstract PipelineEntity getPipeline(ExecuteRequest executeRequest);

    protected abstract ExecuteRequest buildRequest(ExecuteRequest request, PipelineEntity pipeline, ClusterEnum cluster);

    /**
     * 选择运行集群
     *
     * @return
     */
    protected ClusterEnum determineCluster(ExecuteRequest request, PipelineEntity pipelineEntity) {

        if (pipelineEntity.isEnvSupport()) {
            String env = request.getEnv();
            Validator.notNull(env, ErrorCode.EMPTY_ENV);
            EnvGroupEntity envGroup = envDal.findById(pipelineEntity.getEnvGroupId())
                    .orElseThrow(() -> new ExecuteException("环境组不存在！"));

            return Optional.ofNullable(envGroup.getClusterInfo())
                    .orElse(Maps.newHashMap())
                    .getOrDefault(env, ClusterEnum.OFFLINE);

        } else {
            return pipelineEntity.isOnline() ? ClusterEnum.ONLINE : ClusterEnum.OFFLINE;
        }
    }

    ExecuteResult executeOnCluster(ExecuteRequest executeRequest, String url) {
        User user = Context.getUser();
        List<Pair<String, String>> headers = Lists.newArrayList(
                Pair.of(Constants.COOKIE, user.getCookie() == null ? "" : user.getCookie()),
                Pair.of(Constants.USERNAMEZH, URLEncoder.encode(user.getUsernameCN() == null ? "SYSTEM" : user.getUsernameCN())),
                Pair.of(Constants.USERNAME, user.getUsername() == null ? "SYSTEM" : user.getUsername()));

        RequestBody requestBody = OkHttpUtil.buildRequestBody(BodyType.JSON, JSON.toJSONString(executeRequest), null);
        try {

            String offlineResult = OkHttpUtil.resolveResponseBody(OkHttpUtil.call(OkHttpUtil.buildPostRequest(url + "/pipeline/execute", headers, requestBody), 60 * 1000));

            log.info("pipelineId:{},线下集群流水线运行结果:{}", executeRequest.getPipelineId(), offlineResult);
            Response<ExecuteResult> executeResultResponse = JSON.parseObject(offlineResult, new TypeReference<Response<ExecuteResult>>() {
            });

            boolean success = Optional.ofNullable(executeResultResponse)
                    .orElseThrow(() -> new ExecuteException("运行失败！"))
                    .isSuccess();
            if (!success) {
                throw new ExecuteException(executeResultResponse.getMsg());
            }
            return executeResultResponse.getData();

        } catch (ExecuteException ee) {
            log.error("线下集群流水线运行失败,pipelineId:{}", executeRequest.getPipelineId(), ee);
            throw ee;
        } catch (Exception e) {
            log.error("线下集群流水线运行失败,pipelineId:{}", executeRequest.getPipelineId(), e);
            throw new ExecuteException("调用线下集群失败！请稍候重试！");
        }

    }

    ExecuteRequest buildOfflineRequest(ExecuteRequest request, PipelineEntity pipelineEntity) {

        if (pipelineEntity.getFlow() == null || CollectionUtils.isEmpty(pipelineEntity.getFlow().getSteps())) {
            throw new ExecuteException(ErrorCode.EMPTY_FLOW.getDesc());
        }
//        ExecuteRequest request = new ExecuteRequest();
        List<PipelineEntity> allPipelines = Lists.newArrayList(pipelineEntity);
        List<String> importedPipelineIds = pipelineEntity.getFlow().getSteps().stream()
                .filter(step -> step instanceof ImportStepEntity)
                .map(step -> ((ImportStepEntity) step).getPipelineId())
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(importedPipelineIds)) {
            allPipelines.addAll(pipelineDal.queryByIds(importedPipelineIds));
        }

        //组装env 信息
        completeEnvGroup(request, allPipelines);


        //组装instance信息
        completeInstance(request, allPipelines);

        //其他信息组装
        completePipeline(request, allPipelines);

//        request.setEnv(executeRequest.getEnv());
//        request.setPipelineId(executeRequest.getPipelineId());
//        request.setInputs(executeRequest.getInputs());
//        request.setResumeLogId(executeRequest.getResumeLogId());
//        request.setExecType(executeRequest.getExecType());
        return request;
    }

    private void completeEnvGroup(ExecuteRequest request, List<PipelineEntity> pipelineEntities) {
        List<String> envGroupIds = new ArrayList<>();
        pipelineEntities.forEach(pipelineEntity -> {
            if (StringUtils.isNotBlank(pipelineEntity.getEnvGroupId()) && pipelineEntity.isEnvSupport()) {
                envGroupIds.add(pipelineEntity.getEnvGroupId());
            }
        });
        Map<String, EnvGroup> envGroups = request.getEnvGroups();
        if (CollectionUtils.isNotEmpty(envGroupIds)) {
            envDal.findByIds(envGroupIds).forEach(envGroupEntity -> envGroups.put(envGroupEntity.getId(), EnvConverter.entityToDto(envGroupEntity)));
        }

    }

    private void completeInstance(ExecuteRequest request, List<PipelineEntity> pipelineEntities) {
        Set<String> instanceIds = new HashSet<>();

        pipelineEntities.forEach(pipelineEntity -> {
            pipelineEntity.getFlow().getSteps().forEach(step -> {
                if (step instanceof HttpStepEntity) {
                    if (StringUtils.isNotBlank(((HttpStepEntity) step).getInstanceId())) {
                        instanceIds.add(((HttpStepEntity) step).getInstanceId());
                    }
                } else if (step instanceof DubboStepEntity) {
                    if (StringUtils.isNotBlank(((DubboStepEntity) step).getRegister())) {
                        instanceIds.add(((DubboStepEntity) step).getRegister());
                    }
                } else if (step instanceof MysqlStepEntity) {
                    if (StringUtils.isNotBlank(((MysqlStepEntity) step).getInstanceId())) {
                        instanceIds.add(((MysqlStepEntity) step).getInstanceId());
                    }
                } else if (step instanceof RedisStepEntity) {
                    if (StringUtils.isNotBlank(((RedisStepEntity) step).getInstanceId())) {
                        instanceIds.add(((RedisStepEntity) step).getInstanceId());
                    }
                } else if (step instanceof ESStepEntity) {
                    if (StringUtils.isNotBlank(((ESStepEntity) step).getInstanceId())) {
                        instanceIds.add(((ESStepEntity) step).getInstanceId());
                    }
                }
            });
        });

        if (instanceIds.size() > 0) {
            instanceDal.findByIds(instanceIds)
                    .forEach(instance -> request.getInstances().put(instance.getId(), InstanceConverter.entityToDto(instance)));
        }

    }

    private void completePipeline(ExecuteRequest request, List<PipelineEntity> pipelineEntities) {
        pipelineEntities.forEach(pipelineEntity -> request.getPipelines().put(pipelineEntity.getId(), PipelineConverter.entityToDto(pipelineEntity)));
    }

}
