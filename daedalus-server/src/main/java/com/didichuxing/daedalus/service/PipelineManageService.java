package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
import com.didichuxing.daedalus.common.enums.permission.EditableEnum;
import com.didichuxing.daedalus.common.enums.permission.VisibleEnum;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.dal.PipelineSnapshotDal;
import com.didichuxing.daedalus.dal.UserDal;
import com.didichuxing.daedalus.entity.UserEntity;
import com.didichuxing.daedalus.entity.pipeline.PermissionEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineSnapshotEntity;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.PipelineUtils;
import com.didichuxing.daedalus.util.Validator;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author : jiangxinyu
 * @date : 2020/3/23
 */
@Service
@Slf4j
public class PipelineManageService {

    @Autowired
    private PipelineDal pipelineDal;
    @Autowired
    private PipelineSnapshotDal pipelineSnapshotDal;
    @Autowired
    private UserDal userDal;
    @Value("#{'${admin.list}'.split(',')}")
    private List<String> adminList;


    @CachePut(cacheNames = CacheConfig.CACHE_NAME_PIPELINE, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_LIST,
                    CacheConfig.CACHE_NAME_PIPELINE_FAV,
                    CacheConfig.CACHE_NAME_PIPELINE_USED,
                    CacheConfig.CACHE_NAME_PIPELINE_POPULAR,
                    CacheConfig.CACHE_NAME_PIPELINE_CREATOR}, allEntries = true),
    })
    public Pipeline create(Pipeline pipeline) {
        if (pipeline == null) {
            return null;
        }
        log.info("新增pipeline:{} ", pipeline.getName());
        PipelineUtils.valid(pipeline, pipelineIds -> pipelineDal.queryByIds(pipelineIds));

        PipelineEntity pipelineEntity = PipelineConverter.dtoToEntity(pipeline);

        setUser(pipelineEntity);
        PipelineEntity entity = pipelineDal.insert(pipelineEntity);
        return PipelineConverter.entityToDto(entity);
    }

    @CachePut(cacheNames = CacheConfig.CACHE_NAME_PIPELINE, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_LIST,
                    CacheConfig.CACHE_NAME_PIPELINE_FAV,
                    CacheConfig.CACHE_NAME_PIPELINE_CREATOR}, allEntries = true),
    })
    public Pipeline copy(String pipelineId) {
        log.info("复制pipelineId:{}", pipelineId);

        PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
        Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);

        pipelineEntity.setId(null);
        pipelineEntity.setName(pipelineEntity.getName() + "的副本");
        pipelineEntity.setCreateTime(null);
        pipelineEntity.setUpdateTime(null);
        setUser(pipelineEntity);

        PipelineEntity copiedEntity = pipelineDal.insert(pipelineEntity);
        return PipelineConverter.entityToDto(copiedEntity);
    }

    //    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_PIPELINE, key = "#pipelineId")
    public Pipeline detail(String pipelineId) {
        PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
        Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);
        checkViewPermission(pipelineEntity);

        Pipeline pipeline = PipelineConverter.entityToDto(pipelineEntity);
        UserEntity userEntity = userDal.queryUser(Context.getUser().getUsername());
        if (userEntity != null && userEntity.getFavorites() != null && userEntity.getFavorites().contains(pipelineId)) {
            pipeline.setCollect(true);
        }
        return pipeline;
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_PIPELINE, key = "#pi.id"),
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_LIST,
                    CacheConfig.CACHE_NAME_PIPELINE_FAV,
                    CacheConfig.CACHE_NAME_PIPELINE_USED,
                    CacheConfig.CACHE_NAME_PIPELINE_POPULAR,
                    CacheConfig.CACHE_NAME_PIPELINE_CREATOR}, allEntries = true),
    })
    public boolean update(Pipeline pi) {
        Validator.notBlank(pi.getId(), ErrorCode.MISS_PARAM);
        PipelineUtils.valid(pi, pipelineIds -> pipelineDal.queryByIds(pipelineIds));


        PipelineEntity pipelineEntity = pipelineDal.queryById(pi.getId());
        Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);
        checkEditPermission(pipelineEntity);

        //存snapshot
        PipelineSnapshotEntity pipelineSnapshotEntity = new PipelineSnapshotEntity();
        pipelineSnapshotEntity.setPipeline(pipelineEntity);
        pipelineSnapshotEntity.setOperator(Context.getUser().getUsername());
        pipelineSnapshotDal.insert(pipelineSnapshotEntity);

        PipelineEntity entity = PipelineConverter.dtoToEntity(pi);

        //不能修改以下字段
//        entity.setOwner(pipelineEntity.getOwner());
        entity.setCreator(pipelineEntity.getCreator());
        entity.setCreatorCN(pipelineEntity.getCreatorCN());
        entity.setCreateTime(pipelineEntity.getCreateTime());

        return pipelineDal.update(entity);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_PIPELINE, key = "#pipelineId"),
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_LIST,
                    CacheConfig.CACHE_NAME_PIPELINE_FAV,
                    CacheConfig.CACHE_NAME_PIPELINE_USED,
                    CacheConfig.CACHE_NAME_PIPELINE_POPULAR,
                    CacheConfig.CACHE_NAME_PIPELINE_CREATOR}, allEntries = true),
    })
    public void delete(String pipelineId) {
        PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
        Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);
        checkEditPermission(pipelineEntity);
        pipelineDal.delete(pipelineId);
    }

    private void setUser(PipelineEntity pipelineEntity) {
        User user = Context.getUser();
        pipelineEntity.setCreatorCN(user.getUsernameCN());
        pipelineEntity.setCreator(user.getUsername());
//        pipelineEntity.setOwner(Lists.newArrayList(user.getUsername()));
    }

    private void checkEditPermission(PipelineEntity pipelineEntity) {
        PermissionEntity permission = pipelineEntity.getPermission();
        if (permission == null || permission.getEditable() == null || permission.getEditable() == EditableEnum.ALL.getCode()) {
            return;
        }
        String username = Context.getUser().getUsername();
        Integer editable = permission.getEditable();

        Set<String> persons = Sets.newHashSet(pipelineEntity.getCreator());
        persons.addAll(adminList);

        if (editable == EditableEnum.SCOPE.getCode() && permission.getEditors() != null) {
            persons.addAll(permission.getEditors());
        }

        if (!persons.contains(username)) {
            throw new BizException("无编辑权限！请联系" + String.join(",", persons));
        }

    }


    private void checkViewPermission(PipelineEntity pipelineEntity) {
        PermissionEntity permission = pipelineEntity.getPermission();
        if (permission == null || permission.getVisible() == null || permission.getVisible() == VisibleEnum.ALL.getCode()) {
            return;
        }

        Integer visible = permission.getVisible();
        String username = Context.getUser().getUsername();
        Set<String> persons = Sets.newHashSet(pipelineEntity.getCreator());
        persons.addAll(adminList);


        if (visible == EditableEnum.SCOPE.getCode() && permission.getRunners() != null) {
            persons.addAll(permission.getRunners());
        }

        if (!persons.contains(username)) {
            throw new BizException("无运行权限！请联系" + String.join(",", persons));
        }
    }




}
