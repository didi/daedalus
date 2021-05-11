package com.didichuxing.daedalus.client;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.client.util.OkHttpUtil;
import lombok.SneakyThrows;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/9/11
 */
public class DaedalusClient {
    /**
     * 调用数据工厂流水线
     *
     * @param pipelineId 流水线id
     * @param params     流水线参数，key用实际变量名
     * @param env        环境 没有可不填
     * @return
     */
    @SneakyThrows
    public static com.didichuxing.daedalus.client.Response call(String pipelineId, Map<String, String> params, String env) {
        if (pipelineId == null || pipelineId.length() == 0) {
            throw new RuntimeException("pipelineId不能为空！");
        }
        HashMap<String, Object> body = new HashMap<>();
        body.put("pipelineId", pipelineId);
        if (env != null) {
            body.put("env", env);
        }

        if (params != null) {
            body.put("inputs", params);
        }

        Map<String, String> headers = new HashMap() {{
            put("username", "DaedalusClient");
            put("usernamezh", "DaedalusClient");
        }};

        RequestBody requestBody = OkHttpUtil.buildRequestBody(JSON.toJSONString(body));
        Request request = OkHttpUtil.buildPostRequest("http://127.0.0.1:8080/dispatch/call", headers, requestBody);
        String result = OkHttpUtil.resolveResponseBody(OkHttpUtil.call(request, 180000));
        return JSON.parseObject(result, com.didichuxing.daedalus.client.Response.class);
    }

    public static com.didichuxing.daedalus.client.Response call(String pipelineId) {
        return call(pipelineId, null, null);
    }
}
