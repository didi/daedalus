package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.dal.adaptor.InstanceDalAdaptor;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author : jiangxinyu
 * @date : 2020/4/15
 */
@Service
@Slf4j
public class InstanceCenter {

    @Autowired
    private InstanceDalAdaptor instanceDal;

    private static final Integer CACHE_HOUR = 30;


    private final LoadingCache<String, JedisPool> REDIS_INSTANCE = CacheBuilder.newBuilder()
            .removalListener((RemovalListener<String, JedisPool>) notification -> notification.getValue().close())
            .expireAfterAccess(CACHE_HOUR, TimeUnit.MINUTES)
            .build(new CacheLoader<String, JedisPool>() {
                @Override
                public JedisPool load(String instanceId) {
                    return getJedisPool(instanceId);
                }
            });

    private final LoadingCache<String, HikariDataSource> MYSQL_INSTANCE = CacheBuilder.newBuilder()
            .removalListener((RemovalListener<String, HikariDataSource>) notification -> notification.getValue().close())
            .expireAfterAccess(CACHE_HOUR, TimeUnit.MINUTES)
            .build(new CacheLoader<String, HikariDataSource>() {
                @Override
                public HikariDataSource load(String instanceId) {
                    return getMysqlDatasource(instanceId);
                }
            });


    private final LoadingCache<String, List<RegistryConfig>> REGISTRY_INSTANCE = CacheBuilder.newBuilder()
            .expireAfterAccess(CACHE_HOUR, TimeUnit.MINUTES)
            .build(new CacheLoader<String, List<RegistryConfig>>() {
                @Override
                public List<RegistryConfig> load(String instanceId) {
                    final InstanceEntity instance = instanceDal.findByIdAndType(instanceId, InstanceTypeEnum.REGISTRY);
                    if (instance == null) {
                        return null;
                    }
                    RegistryConfig registry = new RegistryConfig();
                    registry.setAddress(instance.getIp() + ":" + instance.getPort() + "," + instance.getIp() + ":" + instance.getPort());
                    registry.setProtocol(instance.getProtocol());
                    return Arrays.asList(registry, registry);
                }
            });


    private HikariDataSource getMysqlDatasource(String instanceId) {
        InstanceEntity instance = instanceDal.findByIdAndType(instanceId, InstanceTypeEnum.MYSQL);
        if (instance == null) {
            log.error("实例 {} 不存在！", instanceId);
            return null;
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true", instance.getIp(), instance.getPort(), instance.getDatabase()));
        hikariConfig.setUsername(instance.getUsername());
        hikariConfig.setPassword(instance.getPassword());
        hikariConfig.setMaxLifetime(1000 * 60 * 10);

        return new HikariDataSource(hikariConfig);
    }


    public Jedis getRedis(String instanceId) {
        try {
            return REDIS_INSTANCE.get(instanceId).getResource();
        } catch (ExecutionException e) {
            log.error("get redis instance error!", e);
            throw new ExecuteException("获取Redis实例失败", e);
        }
    }


    public List<RegistryConfig> getRegistry(String instanceId) {
        try {
            return this.REGISTRY_INSTANCE.get(instanceId);
        } catch (ExecutionException e) {
            log.error("get registry instance error!", e);
            throw new ExecuteException("获取Registry实例失败", e);
        }
    }

    public Connection getMysql(String instanceId) {
        try {
            return MYSQL_INSTANCE.get(instanceId).getConnection();
        } catch (ExecutionException | SQLException e) {
            log.error("get mysql instance error!", e);
            throw new ExecuteException("获取Mysql实例失败", e);
        }
    }

    public InstanceEntity getInstance(String instanceId, InstanceTypeEnum type) {
        return instanceDal.findByIdAndType(instanceId, type);
    }

    private JedisPool getJedisPool(String instanceId) {
        InstanceEntity instanceEntity = instanceDal.findByIdAndType(instanceId, InstanceTypeEnum.REDIS);
        if (instanceEntity == null) {
            log.error("实例 {} 不存在！", instanceId);
            return null;
        }
        return new JedisPool(instanceEntity.getIp(), instanceEntity.getPort());
    }

    public void invalidate(String instanceId) {
        REDIS_INSTANCE.invalidate(instanceId);
        MYSQL_INSTANCE.invalidate(instanceId);
        REGISTRY_INSTANCE.invalidate(instanceId);
    }

}
