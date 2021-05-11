package com.didichuxing.daedalus.config;

import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import com.didichuxing.daedalus.entity.log.LogEntity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.util.ClassTypeInformation;

/**
 * @author : jiangxinyu
 * @date : 2020/4/17
 */
@Configuration
public class MongoConfiguration {

    @Bean
    public MappingMongoEntityInformation<InstanceEntity, String> instanceGroupInfo() {
        return get(InstanceEntity.class);
    }


    @Bean
    public MappingMongoEntityInformation<EnvGroupEntity, String> envGroupInfo() {
        return get(EnvGroupEntity.class);
    }

    @Bean
    public MappingMongoEntityInformation<LogEntity, String> logInfo() {
        return get(LogEntity.class);
    }

    private <T> MappingMongoEntityInformation<T, String> get(Class<T> clazz) {
        ClassTypeInformation<T> info = ClassTypeInformation.from(clazz);
        BasicMongoPersistentEntity<T> entity = new BasicMongoPersistentEntity<>(info);
        return new MappingMongoEntityInformation<>(entity);
    }
}
