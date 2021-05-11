package com.didichuxing.daedalus.common.dto.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import redis.clients.jedis.Protocol;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class RedisStep extends BaseStep {
    @NotBlank(message = "redis实例不能为空！")
    private String instanceId;
    @NotNull
    private Protocol.Command operationType;
    private String command;

}
