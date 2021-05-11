package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.EnvDal;
import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import com.didichuxing.daedalus.handler.EnvConverter;
import com.didichuxing.daedalus.util.Context;
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
public class EnvService {

    @Autowired
    private EnvDal envDal;

    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_ENV_LIST, key = "#name+'-'+#page+'-'+#pageSize")
    public PageResponse<List<EnvGroup>> list(String name, int page, int pageSize) {
        Page<EnvGroupEntity> all = envDal.findAll(name, page, pageSize);

        List<EnvGroup> envGroups = all.stream().map(EnvConverter::entityToDto).collect(Collectors.toList());
        return PageResponse.of(page, pageSize, all.getTotalPages(), all.getTotalElements(), envGroups);
    }

    @CachePut(cacheNames = CacheConfig.CACHE_NAME_ENV, key = "#result.id")
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_ENV, key = "#result.id"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_ENV_LIST, allEntries = true)
    })
    public EnvGroup save(EnvGroup envGroup) {

        //todo 校验是否被使用
        EnvGroupEntity envGroupEntity = EnvConverter.dtoToEntity(envGroup);
        envGroupEntity.setCreator(Context.getUser().getUsername());
        envGroupEntity.setCreatorCN(Context.getUser().getUsernameCN());
        envGroupEntity = envDal.save(envGroupEntity);
        return EnvConverter.entityToDto(envGroupEntity);
    }

    @Caching(evict = {
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_ENV, key = "#envGroupId"),
            @CacheEvict(cacheNames = CacheConfig.CACHE_NAME_ENV_LIST, allEntries = true)
    })
    public void delete(String envGroupId) {
        envDal.delById(envGroupId);
    }

    @Cacheable(cacheNames = CacheConfig.CACHE_NAME_ENV, key = "#envGroupId")
    public EnvGroup detail(String envGroupId) {
        return EnvConverter.entityToDto(envDal.findById(envGroupId).orElse(null));
    }
}
