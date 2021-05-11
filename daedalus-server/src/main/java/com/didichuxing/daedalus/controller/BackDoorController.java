package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Optional;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("backDoor")
@Api(tags = "后门")
public class BackDoorController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("clearCache")
    @ApiOperation("清空缓存")
    public Response<Void> list() {
        Collection<String> cacheNames = cacheManager.getCacheNames();
        for (String cacheName : cacheNames) {
            Optional.ofNullable(cacheManager.getCache(cacheName)).ifPresent(Cache::clear);
        }
        return Response.sucResp();

    }

}
