package com.didichuxing.daedalus.common.enums.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import redis.clients.jedis.Builder;
import redis.clients.jedis.BuilderFactory;
import redis.clients.jedis.Protocol;

/**
 * @author : jiangxinyu
 * @date : 2020/4/26
 */
@Getter
@AllArgsConstructor
public enum RedisOperationType {

    SET(Protocol.Command.SET, BuilderFactory.LONG),
    GET(Protocol.Command.GET, BuilderFactory.STRING),
    SETEX(Protocol.Command.SETEX, BuilderFactory.STRING),
    EXISTS(Protocol.Command.EXISTS, BuilderFactory.LONG),
    DEL(Protocol.Command.DEL, BuilderFactory.LONG),
    HMSET(Protocol.Command.HMSET, BuilderFactory.STRING),
    HMGET(Protocol.Command.HMGET, BuilderFactory.STRING_LIST),
    HGETALL(Protocol.Command.HGETALL, BuilderFactory.STRING_MAP),
    HGET(Protocol.Command.HGET, BuilderFactory.STRING),
    EXPIRE(Protocol.Command.EXPIRE, BuilderFactory.LONG),
    MSET(Protocol.Command.MSET, BuilderFactory.STRING),
    MGET(Protocol.Command.MGET, BuilderFactory.STRING_LIST),
    ;

    private Protocol.Command command;
    private Builder builder;


    public static Builder getBuilder(Protocol.Command command) {
        if (command == null) {
            return null;
        }
        for (RedisOperationType value : RedisOperationType.values()) {
            if (value.name().equalsIgnoreCase(command.name())) {
                return value.builder;
            }
        }
        return null;
    }

    public static Protocol.Command getCommand(String command) {
        if (command == null) {
            return null;
        }
        for (RedisOperationType value : RedisOperationType.values()) {
            if (value.getCommand().name().equalsIgnoreCase(command)) {
                return value.getCommand();
            }
        }

        return null;
    }


}
