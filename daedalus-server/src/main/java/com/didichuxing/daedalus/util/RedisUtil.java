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




}
