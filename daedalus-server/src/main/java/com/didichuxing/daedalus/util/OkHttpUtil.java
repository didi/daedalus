package com.didichuxing.daedalus.util;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.entity.log.StepResponseEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.google.common.collect.Maps;
import lombok.Cleanup;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2019-07-31
 */
@UtilityClass
@Slf4j
public class OkHttpUtil {
    private static final OkHttpClient DEFAULT_CLIENT;
    private static final OkHttpClient REDIRECT_DEFAULT_CLIENT;

    static {
        DEFAULT_CLIENT = new OkHttpClient.Builder()
                .followRedirects(false)
                .followSslRedirects(false)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        REDIRECT_DEFAULT_CLIENT = new OkHttpClient.Builder()
                .followRedirects(true)
                .followSslRedirects(true)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();
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


    public static Response call(Request request, Integer timeoutMs, Boolean followRedirect) throws IOException {
        boolean redirect = followRedirect != null && followRedirect;
        if (timeoutMs == null || timeoutMs <= 10000) {
            if (redirect) {
                return REDIRECT_DEFAULT_CLIENT.newCall(request).execute();
            } else {
                return DEFAULT_CLIENT.newCall(request).execute();
            }
        }
        return new OkHttpClient.Builder()
                .followRedirects(redirect)
                .followSslRedirects(redirect)
                .connectTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .readTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .writeTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                .build()
                .newCall(request)
                .execute();

    }


    public static Request buildPostRequest(String url, List<Pair<String, String>> headers, RequestBody body) {
        Map<String, String> headerMap = headers == null ? Maps.newHashMap() : headers.stream().collect(Collectors.toMap(Pair::getName, Pair::getValue));

        return new Request.Builder()
                .url(url)
                .headers(Headers.of(headerMap))
                .post(body)
                .build();

    }

    public static Request buildPutRequest(String url, List<Pair<String, String>> headers, RequestBody body) {
        Map<String, String> headerMap = headers == null ? Maps.newHashMap() : headers.stream().collect(Collectors.toMap(Pair::getName, Pair::getValue));

        return new Request.Builder()
                .url(url)
                .headers(Headers.of(headerMap))
                .put(body)
                .build();

    }

    public static Request buildDeleteRequest(String url, List<Pair<String, String>> headers, RequestBody body) {
        Map<String, String> headerMap = headers == null ? Maps.newHashMap() : headers.stream().collect(Collectors.toMap(Pair::getName, Pair::getValue));

        return new Request.Builder()
                .url(url)
                .headers(Headers.of(headerMap))
                .delete(body)
                .build();

    }


    public static Request buildGetRequest(String url, List<Pair<String, String>> headers) {
        Map<String, String> headerMap = headers == null ? Maps.newHashMap() : headers.stream().collect(Collectors.toMap(Pair::getName, Pair::getValue));

        return new Request.Builder()
                .headers(Headers.of(headerMap))
                .url(url)
                .get()
                .build();
    }

    /**
     * 拼接带query 的url
     *
     * @param baseUrl
     * @param query
     * @param queryString
     * @return
     */
    public static String buildQueryUrl(String baseUrl, List<Pair<String, String>> query, String queryString) {
        if (StringUtils.endsWith(baseUrl, "/")) {
            baseUrl = StringUtils.removeEnd(baseUrl, "/");
        }
        if (StringUtils.isNotBlank(queryString)) {
            return baseUrl + "?" + queryString;
        } else if (query != null) {
            return baseUrl + "?" + query.stream().map(q -> q.getName() + "=" + q.getValue()).collect(Collectors.joining("&"));
        }
        return baseUrl;
    }


    public static RequestBody buildRequestBody(BodyType bodyType, String body, List<Pair<String, String>> formData) {
        if (bodyType == BodyType.JSON || bodyType == BodyType.TEXT) {
            return RequestBody.create(MediaType.parse(bodyType.getContentType()), body);
        } else if (bodyType == BodyType.FORM_URLENCODED || bodyType == BodyType.FORM_DATA) {
            FormBody.Builder builder = new FormBody.Builder(StandardCharsets.UTF_8);
            if (formData != null) {
                formData.stream().filter(pair -> StringUtils.isNotBlank(pair.getName())).forEach(fd -> builder.add(fd.getName(), fd.getValue()));
            }
            return builder.build();
        }
        throw new ExecuteException("不支持的ContentType！");
    }


    public static StepResponseEntity resolveResponse(Response response) {
        StepResponseEntity stepResponse = new StepResponseEntity();

        if (response == null) {
            stepResponse.setResult("HTTP请求出错！");
            return stepResponse;
        }
        Map<String, List<String>> headers = response.headers().toMultimap();

        @Cleanup ResponseBody body = response.body();
        if (body != null) {
            try {
                stepResponse.setResult(body.string());
            } catch (IOException e) {
                log.error("get http body error", e);
            }
        }
        stepResponse.setHeaders(headers);
        return stepResponse;
    }

    public static String resolveResponseBody(Response response) {
        if (response == null) {
            return null;
        }
        @Cleanup ResponseBody body = response.body();
        if (body != null) {
            try {
                String result = body.string();
                log.info("http code:{}.http result:{}", response.code(), result);
                return result;
            } catch (IOException e) {
                log.error("get http body error", e);
            }
        }
        return null;
    }


}
