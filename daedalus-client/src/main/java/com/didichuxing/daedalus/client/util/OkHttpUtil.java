package com.didichuxing.daedalus.client.util;

import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author : jiangxinyu
 * @date : 2019-07-31
 */
@UtilityClass
@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient DEFAULT_CLIENT;

    static {
        DEFAULT_CLIENT = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(180, TimeUnit.SECONDS).readTimeout(180, TimeUnit.SECONDS).writeTimeout(180, TimeUnit.SECONDS).build();
    }


    public static Response call(Request request, Integer timeoutMs) throws IOException {
        if (timeoutMs == null || timeoutMs <= 10000) {
            return DEFAULT_CLIENT.newCall(request).execute();
        }
        return new OkHttpClient.Builder()
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build()
                .newCall(request)
                .execute();

    }

    public static Request buildPostRequest(String url, Map<String, String> headers, RequestBody body) {
        Map<String, String> headerMap = headers == null ? new HashMap<>() : headers;

        return new Request.Builder()
                .url(url)
                .headers(Headers.of(headerMap))
                .post(body)
                .build();

    }


    public static RequestBody buildRequestBody(String body) {
        return RequestBody.create(MediaType.parse("application/json"), body);

    }


    public static String resolveResponseBody(Response response) {
        if (response == null) {
            return null;
        }
        @Cleanup ResponseBody body = response.body();
        if (body != null) {
            try {
                String result = body.string();
                log.debug("http code:{}.http result:{}", response.code(), result);
                return result;
            } catch (IOException e) {
                log.error("get http body error", e);
            }
        }
        return null;
    }


}
