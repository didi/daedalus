package com.didichuxing.daedalus.common.enums.http;

/**
 * @author : jiangxinyu
 * @date : 2020/4/26
 */
public enum MethodType {
    GET,
    POST,
    PUT,
    DELETE;


    public static MethodType parse(String method) {
        for (MethodType type : MethodType.values()) {
            if (type.name().equalsIgnoreCase(method)) {
                return type;
            }
        }
        return null;
    }
}
