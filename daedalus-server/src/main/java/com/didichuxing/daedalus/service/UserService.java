package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.aop.TimeLog;
import com.didichuxing.daedalus.config.CacheConfig;
import com.didichuxing.daedalus.dal.UserDal;
import com.didichuxing.daedalus.entity.UserEntity;
import com.didichuxing.daedalus.util.Context;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@Service
@Slf4j
public class UserService {
    @Autowired
    private UserDal userDal;


    @Async
    @TimeLog
    @Caching(evict = {
            @CacheEvict(cacheNames = {CacheConfig.CACHE_NAME_PIPELINE_LIST,
                    CacheConfig.CACHE_NAME_PIPELINE_FAV,
                    CacheConfig.CACHE_NAME_PIPELINE_USED,
                    CacheConfig.CACHE_NAME_PIPELINE_POPULAR,
                    CacheConfig.CACHE_NAME_PIPELINE_CREATOR}, allEntries = true),
    })
    public void collect(String pipelineId, Boolean isCollect) {
        String user = Context.getUser().getUsername();
        UserEntity userEntity = userDal.queryUser(user);
        if (userEntity == null) {
            userEntity = new UserEntity();
            userEntity.setUsername(user);
            userEntity.setUsernameCN(Context.getUser().getUsernameCN());
            if (isCollect) {
                userEntity.setFavorites(Lists.newArrayList(pipelineId));
            } else {
                userEntity.setFavorites(new ArrayList<>());
            }
        } else {
            if (userEntity.getFavorites() != null) {
                if (isCollect) {
                    userEntity.getFavorites().add(pipelineId);
                } else {
                    userEntity.getFavorites().remove(pipelineId);
                }
            }
        }
        log.info("更新username:{}收藏夹:{}", userEntity.getUsername(), userEntity.getFavorites());
        userDal.save(userEntity);


    }
}
