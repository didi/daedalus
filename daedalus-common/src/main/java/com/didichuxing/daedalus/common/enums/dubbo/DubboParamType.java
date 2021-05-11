package com.didichuxing.daedalus.common.enums.dubbo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * @author : jiangxinyu
 * @date : 2020/5/8
 */
@AllArgsConstructor
@Getter
public enum DubboParamType {

    STRING(String.class, v -> v),
    BOOLEAN(Boolean.class, v -> v),
    boolean_(boolean.class, Boolean::parseBoolean),
    INTEGER(Integer.class, v -> v),
    int_(int.class, Integer::parseInt),
    LONG(Long.class, v -> v),
    long_(long.class, Long::parseLong),
    DOUBLE(Double.class, v -> v),
    double_(double.class, Double::parseDouble),
    FLOAT(Float.class, v -> v),
    float_(float.class, Float::parseFloat),
    SHORT(Short.class, v -> v),
    short_(short.class, Short::parseShort),
    LIST(List.class, null),
    MAP(Map.class, null);

    private Class<?> clazz;
    private Function<String, Object> converter;

    public static DubboParamType getType(String clazzName) {
        for (DubboParamType type : values()) {
            if (type.getClassName().equals(clazzName)) {
                return type;
            }
        }
        return null;
    }

    public String getClassName() {
        return this.clazz.getName();
    }

    public static boolean isPrimitiveWarp(String type) {
        return Stream.of(String.class, Boolean.class,
                Integer.class, Long.class,
                Float.class, Double.class,
                Character.class, Byte.class,
                Short.class, Void.class)
                .anyMatch(clazz -> clazz.getName().equals(type));

    }

    public static boolean isPrimitiveBasic(String type) {
        return Stream.of(boolean.class,
                int.class, long.class,
                float.class, double.class,
                char.class, byte.class,
                short.class, void.class)
                .anyMatch(clazz -> clazz.getName().equals(type));

    }
}
