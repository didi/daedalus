package com.didichuxing.daedalus.handler;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/27
 */
@UtilityClass
@Slf4j
public class SimpleConverter {

    public static <T> T convert(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T t;
        try {
            t = target.newInstance();
        } catch (Exception e) {
            log.error("创建bean失败!", e);
            return null;
        }
        BeanUtils.copyProperties(source, t);
        return t;
    }

    /**
     * 简单的list转换
     *
     * @param list
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> List<T> convertList(List<?> list, Class<T> clazz) {
        if (list == null || list.size() == 0) {
            return Collections.emptyList();
        }
        return list.stream().map(obj -> convert(obj, clazz)).collect(Collectors.toList());
    }
}
