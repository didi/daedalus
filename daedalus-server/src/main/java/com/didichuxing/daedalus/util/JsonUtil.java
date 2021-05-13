package com.didichuxing.daedalus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.JSONValidator;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : jiangxinyu
 * @date : 2020/4/30
 */
@UtilityClass
@Slf4j
public class JsonUtil {

    public static String getPath(String obj, String path) {
        try {
            Object extract = JSONPath.extract(obj, path);
            return extract == null ? "" : String.valueOf(extract);
        } catch (Exception e) {
            log.error("根据JSON path获取结果失败", e);
            throw new ExecuteException("json path:" + path + "非法！");
        }
    }

    public static boolean isJson(String string) {
        return JSONValidator.from(string).validate();
    }

    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }





}
