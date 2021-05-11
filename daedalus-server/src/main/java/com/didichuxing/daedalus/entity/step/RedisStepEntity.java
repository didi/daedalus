package com.didichuxing.daedalus.entity.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;
import redis.clients.jedis.Protocol;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class RedisStepEntity extends BaseStepEntity {

    @Field
    private String instanceId;
    @Field
    private Protocol.Command operationType;
    @Field
    private String command;


}
