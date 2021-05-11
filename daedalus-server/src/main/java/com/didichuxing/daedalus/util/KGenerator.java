package com.didichuxing.daedalus.util;

import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/5/12
 */
public class KGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder sb = new StringBuilder(Context.getUser().getUsername()).append("-");
        for (Object param : params) {
            if (param instanceof List) {
                sb.append(((List) param).stream().map(String::valueOf).collect(Collectors.joining("-")));
            } else {
                sb.append(param);
            }
        }
        return sb.toString();
    }
}
