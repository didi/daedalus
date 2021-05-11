package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.aop.TimeLog;
import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.pipeline.PipelineInfo;
import com.didichuxing.daedalus.common.enums.permission.EditableEnum;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.LogDal;
import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.dal.UserDal;
import com.didichuxing.daedalus.entity.UserEntity;
import com.didichuxing.daedalus.entity.pipeline.PermissionEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.handler.SimpleConverter;
import com.didichuxing.daedalus.util.Context;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/3/25
 */
@Service
@Slf4j
public class PipelineQueryService {
    @Autowired
    private PipelineDal pipelineDal;
    @Autowired
    private UserDal userDal;
    @Autowired
    private LogDal logDal;
    @Value("#{'${admin.list}'.split(',')}")
    private List<String> adminList;

    /**
     * 查询最近使用的
     */
    @TimeLog
    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE_USED, keyGenerator = "keyGenerator")
    public PageResponse<List<PipelineInfo>> queryRecentUsed(List<Integer> bizLine, List<String> tags, int page, int size) {
        String user = Context.getUser().getUsername();
        List<String> favorites = getFavorite(user);
        Page<PipelineDal.PipelineRecord> result = pipelineDal.queryRecentUsed(user, bizLine, tags, page, size);
        List<PipelineInfo> pipelines = result
                .stream()
                .map(pr -> {
                    PipelineInfo pipelineInfo = SimpleConverter.convert(pr.getPipeline(), PipelineInfo.class);
                    pipelineInfo.setExecuteTime(pr.getLog().getCreateTime());
                    pipelineInfo.setCollect(favorites.contains(pr.getPipeline().getId()));
                    return pipelineInfo;
                })
                .collect(Collectors.toList());
        return PageResponse.of(result, pipelines);
    }

    @TimeLog
    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE_CREATOR, keyGenerator = "keyGenerator")
    public PageResponse<List<PipelineInfo>> queryByCreator(List<Integer> bizLine, List<String> tags, int page, int size) {
        String user = Context.getUser().getUsername();
        Page<PipelineEntity> result = pipelineDal.queryBy(user, bizLine, tags, page, size);

        List<PipelineInfo> pipelines = result.getContent()
                .stream()
                .map(entity -> {
                    PipelineInfo pipelineInfo = PipelineConverter.entityToInfo(entity);
                    pipelineInfo.setEditable(true);
                    return pipelineInfo;
                })
                .collect(Collectors.toList());
        return PageResponse.of(result, isCollected(pipelines));
    }

    @TimeLog
    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE_FAV, keyGenerator = "keyGenerator")
    public PageResponse<List<PipelineInfo>> queryFavorites(List<Integer> bizLine, List<String> tags, int page, int size) {
        String username = Context.getUser().getUsername();
        UserEntity userEntity = userDal.queryUser(username);
        if (userEntity == null || CollectionUtils.isEmpty(userEntity.getFavorites())) {
            return PageResponse.of(page, size, 0, 0, Collections.emptyList());
        }
        Page<PipelineEntity> result = pipelineDal.queryByIds(userEntity.getFavorites(), bizLine, tags, page, size);
        List<PipelineInfo> pipelines = result
                .getContent()
                .stream()
                .map(entity -> {
                    PipelineInfo pipelineInfo = PipelineConverter.entityToInfo(entity);
                    pipelineInfo.setEditable(false);
                    return pipelineInfo;
                })
                .peek(pipeline -> pipeline.setCollect(true))
                .collect(Collectors.toList());
        return PageResponse.of(result, pipelines);
    }

    @TimeLog
    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE_POPULAR, keyGenerator = "keyGenerator")
    public PageResponse<List<PipelineInfo>> queryPopular(List<Integer> bizLine, List<String> tags, int page, int size) {
        List<String> pipelineIds = logDal.queryByCount(page, size).getContent();
        Page<PipelineEntity> result = pipelineDal.queryByIds(pipelineIds, bizLine, tags, page, size);
        List<PipelineInfo> pipelineInfos = result.getContent()
                .stream()
                .map(entity -> {
                    PipelineInfo info = PipelineConverter.entityToInfo(entity);
                    PipelineInfo pipelineInfo = PipelineConverter.entityToInfo(entity);
                    pipelineInfo.setEditable(false);
                    return info;
                }).collect(Collectors.toList());
        return PageResponse.of(result, pipelineInfos);
    }

    @TimeLog
    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE_LIST, keyGenerator = "keyGenerator")
    public PageResponse<List<PipelineInfo>> list(List<Integer> bizLine, List<String> tags, int page, int size) {
        Page<PipelineEntity> result = pipelineDal.queryAll(bizLine, tags, page, size);
        List<PipelineInfo> pipelines = result
                .stream()
                .map(PipelineConverter::entityToInfo)
                .collect(Collectors.toList());
        return PageResponse.of(result, isCollected(pipelines));
    }

    public PageResponse<List<PipelineInfo>> searchByTag(List<String> tags, int page, int size) {
        Page<PipelineEntity> result = pipelineDal.searchByTag(tags, page, size);
        List<PipelineInfo> pipelines = result
                .stream()
                .map(PipelineConverter::entityToInfo)
                .collect(Collectors.toList());
        return PageResponse.of(result, isCollected(pipelines));
    }


    public List<PipelineInfo> search(String key) {
        return pipelineDal.search(key)
                .stream().map(PipelineConverter::entityToInfo)
                .collect(Collectors.toList());
    }


    private List<PipelineInfo> isCollected(List<PipelineInfo> pipelines) {
        String username = Context.getUser().getUsername();
        UserEntity userEntity = userDal.queryUser(username);
        if (userEntity == null || CollectionUtils.isEmpty(userEntity.getFavorites()) || CollectionUtils.isEmpty(pipelines)) {
            return pipelines;
        }
        pipelines.forEach(pipeline -> pipeline.setCollect(userEntity.getFavorites().contains(pipeline.getId())));
        return pipelines;
    }


    private List<String> getFavorite(String username) {
        UserEntity userEntity = userDal.queryUser(username);
        return (userEntity == null || userEntity.getFavorites() == null) ? new ArrayList<>() : userEntity.getFavorites();
    }

    public boolean permit(String pipelineId) {
        PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
        PermissionEntity permission = pipelineEntity.getPermission();
        if (permission == null || permission.getEditable() == null || permission.getEditable() == EditableEnum.ALL.getCode()) {
            return true;
        }
        String username = Context.getUser().getUsername();
        Integer editable = permission.getEditable();

        Set<String> persons = Sets.newHashSet(pipelineEntity.getCreator());
        persons.addAll(adminList);

        if (editable == EditableEnum.SCOPE.getCode() && permission.getEditors() != null) {
            persons.addAll(permission.getEditors());
        }

        return persons.contains(username);
    }

    public Response<List<PipelineInfo>> searchByCreator(String creator, int page, int pageSize) {
        Page<PipelineEntity> result = pipelineDal.queryBy(creator, null, null, page, pageSize);
        List<PipelineInfo> pipelines = result
                .stream()
                .map(PipelineConverter::entityToInfo)
                .collect(Collectors.toList());
        return PageResponse.of(result, isCollected(pipelines));
    }
}
