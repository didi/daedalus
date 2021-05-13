package com.didichuxing.daedalus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.step.HttpStep;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.didichuxing.daedalus.common.enums.http.UrlType;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import okhttp3.Request;
import okhttp3.Response;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : jiangxinyu
 * @date : 2020/7/16
 */
@UtilityClass
public class SwaggerUtil {

    @SneakyThrows
    public static List<HttpStep> parseSwagger(String url) {
        Request request = OkHttpUtil.buildGetRequest(url + "/v2/api-docs", null);
        Response response = OkHttpUtil.call(request, 5000);
        String apiDocsStr = OkHttpUtil.resolveResponseBody(response);
        JSONObject apiDocs = JSON.parseObject(apiDocsStr);
        JSONObject paths = apiDocs.getJSONObject("paths");

        return paths.keySet().stream()
                .map(path -> {
                    JSONObject onePath = paths.getJSONObject(path);
                    return onePath.keySet().stream()
                            .map(method -> {
                                JSONObject requestInfo = onePath.getJSONObject(method);
                                requestInfo.put("method", method);
                                requestInfo.put("url", apiDocs.getString("host") + path);
                                return requestInfo;
                            });

                })
                .flatMap((Function<Stream<JSONObject>, Stream<JSONObject>>) requestStream -> requestStream)
                .map(req -> {
                    HttpStep httpStep = new HttpStep();
                    httpStep.setUrlType(UrlType.INPUT);
                    httpStep.setUrl(req.getString("url"));
                    httpStep.setMethod(MethodType.parse(req.getString("method ")));
                    Optional.ofNullable(req.getString("summary"))
                            .ifPresent(httpStep::setName);

                    Optional.ofNullable(req.getJSONArray("parameters"))
                            .ifPresent(parameters -> {
                                for (int i = 0; i < parameters.size(); i++) {
                                    JSONObject parameter = parameters.getJSONObject(i);
                                    String in = parameter.getString("in");
                                    if ("header".equals(in)) {
                                        List<Pair<String, String>> headers = httpStep.getHeaders();
                                        if (headers == null) {
                                            headers = Lists.newArrayList();
                                            httpStep.setHeaders(headers);
                                        }
                                        headers.add(Pair.of(parameter.getString("name"), ""));
                                    } else if ("body".equals(in)) {
                                        httpStep.setBody("{}");
                                    } else if ("query".equals(in)) {
                                        List<Pair<String, String>> urlParams = httpStep.getUrlParams();
                                        if (urlParams == null) {
                                            urlParams = Lists.newArrayList();
                                            httpStep.setUrlParams(urlParams);
                                        }
                                        urlParams.add(Pair.of(parameter.getString("name"), ""));
                                    }
                                }
                            });
                    return httpStep;
                })
                .collect(Collectors.toList());


    }





}
