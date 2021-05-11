package com.didichuxing.daedalus.config;

import com.didichuxing.daedalus.util.KGenerator;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : jiangxinyu
 * @date : 2020/5/12
 */
@Configuration
public class CacheConfig {

    public static final String CACHE_NAME_PIPELINE = "pipeline";
    public static final String CACHE_NAME_PIPELINE_LIST = "pipelineList";
    public static final String CACHE_NAME_PIPELINE_CREATOR = "pipelineCreator";
    public static final String CACHE_NAME_PIPELINE_USED = "pipelineUsed";
    public static final String CACHE_NAME_PIPELINE_POPULAR = "pipelinePopular";
    public static final String CACHE_NAME_PIPELINE_FAV = "pipelineFav";
    public static final String CACHE_NAME_ENV = "env";
    public static final String CACHE_NAME_ENV_LIST = "envList";
    public static final String CACHE_NAME_INSTANCE = "instance";
    public static final String CACHE_NAME_INSTANCE_LIST = "instanceList";
    public static final String CACHE_NAME_LOG = "log";

    @Bean
    public CacheManager cacheManager() {
        //暂时使用本地缓存
        return new ConcurrentMapCacheManager(CACHE_NAME_PIPELINE,
                CACHE_NAME_PIPELINE_LIST,
                CACHE_NAME_PIPELINE_CREATOR,
                CACHE_NAME_PIPELINE_POPULAR,
                CACHE_NAME_PIPELINE_USED,
                CACHE_NAME_PIPELINE_FAV,
                CACHE_NAME_ENV,
                CACHE_NAME_ENV_LIST,
                CACHE_NAME_INSTANCE,
                CACHE_NAME_LOG,
                CACHE_NAME_INSTANCE_LIST);
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KGenerator();
    }
}
