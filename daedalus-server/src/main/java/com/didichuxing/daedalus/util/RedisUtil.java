package com.didichuxing.daedalus.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Protocol;

import java.util.Arrays;

/**
 * @author : jiangxinyu
 * @date : 2020/4/14
 */
@Slf4j
@UtilityClass
public class RedisUtil {


    public static String[] getArgs(String command) {
        String[] parts = command.split(" ");
        return Arrays.copyOfRange(parts, 1, parts.length);
    }

    public static String getCommand(String command) {
        String[] parts = command.split(" ");
        return parts[0];
    }


    public static void main(String[] args) {
        long l = System.currentTimeMillis();
//        RedisClient redisClient = RedisClient.create("redis://10.96.88.82:6379");
//        StatefulRedisConnection<String, String> connect = redisClient.connect();
//        RedisCommands<String, String> sync = connect.sync();
//        System.out.println(System.currentTimeMillis() - l);

        long s = System.currentTimeMillis();

        JedisPool jedisPool = new JedisPool("10.96.88.82", 6379);
        Jedis resource = jedisPool.getResource();
        Object o = resource.sendCommand(Protocol.Command.HSET, "test", "name", "a");
        Object o2 = resource.sendCommand(Protocol.Command.HGET, "test", "name");
        Object o3 = resource.sendCommand(Protocol.Command.HGETALL, "test");

        System.out.println(o);
        System.out.println(o2);
        System.out.println(System.currentTimeMillis() - s);
    }
}
