package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.dto.instance.Instance;
import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.InstanceDal;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import com.didichuxing.daedalus.handler.InstanceConverter;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.RegexUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/15
 */
@Service
public class InstanceService {

    @Autowired
    private InstanceDal instanceDal;
    @Autowired
    private InstanceCenter instanceCenter;


    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_INSTANCE_LIST, key = "#insType+'-'+#page+'-'+#pageSize+'-'+#name+'-'+#ip")
    public PageResponse<List<Instance>> list(String insType, int page, int pageSize, String name, String ip) {
        insType = "fusion".equalsIgnoreCase(insType) ? "REDIS" : insType;
        Page<InstanceEntity> instanceEntities = instanceDal.list(insType, page, pageSize, name, ip);
        List<Instance> instances = instanceEntities.stream().map(InstanceConverter::entityToDto).collect(Collectors.toList());
        return PageResponse.of(page, pageSize, instanceEntities.getTotalPages(), instanceEntities.getTotalElements(), instances);
    }

    @CachePut(cacheNames = CacheConfig.CACHE_NAME_INSTANCE, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_INSTANCE, key = "#result.id"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_INSTANCE_LIST, allEntries = true)})
    public Instance save(Instance instance) {
        if (instance.getInstanceType() == InstanceTypeEnum.HTTP) {
            RegexUtil.validUrl(instance.getUrl());
        }
        InstanceEntity instanceEntity = InstanceConverter.dtoToEntity(instance);
        instanceEntity.setCreator(Context.getUser().getUsername());
        instanceEntity.setCreatorCN(Context.getUser().getUsernameCN());
        InstanceEntity savedEntity = instanceDal.save(instanceEntity);
        instanceCenter.invalidate(savedEntity.getId());
        return InstanceConverter.entityToDto(savedEntity);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_INSTANCE, key = "#instanceId"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_INSTANCE_LIST, allEntries = true)})
    public void delete(String instanceId) {
        instanceDal.delete(instanceId);
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_INSTANCE, key = "#instanceId")
    public Instance detail(String instanceId) {
        return InstanceConverter.entityToDto(instanceDal.findById(instanceId));
    }
}
