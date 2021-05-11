package com.didichuxing.daedalus.service.executor;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.didichuxing.daedalus.common.enums.http.UrlType;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import com.didichuxing.daedalus.entity.log.StepResponseEntity;
import com.didichuxing.daedalus.entity.step.HttpStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.InstanceCenter;
import com.didichuxing.daedalus.service.VariableProcessor;
import com.didichuxing.daedalus.util.OkHttpUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class HttpExecutor extends Executor<HttpStepEntity> {

    @Autowired
    private InstanceCenter instanceCenter;
    @Autowired
    private VariableProcessor variableProcessor;

    @Override
    public void exec(PipelineContext pipelineContext, HttpStepEntity step) {

        HttpStepEntity template = determineTemplate(step);
        //模板中可能使用了变量
        variableProcessor.process(template, pipelineContext.getVars());
        String url = getUrl(template.getUrl(), template.getUrlParams());
        List<Pair<String, String>> headers = resolveHeaders(template);

        //log
        log(pipelineContext, step, url, headers);

        Request request;
        try {
            MethodType method = step.getMethod();
            switch (method) {
                case GET:
                    request = OkHttpUtil.buildGetRequest(url, headers);
                    break;
                case POST:
                    request = OkHttpUtil.buildPostRequest(url, headers, OkHttpUtil.buildRequestBody(step.getBodyType(), step.getBody(), step.getFormData()));
                    break;
                case DELETE:
                    request = OkHttpUtil.buildDeleteRequest(url, headers, OkHttpUtil.buildRequestBody(step.getBodyType(), step.getBody(), step.getFormData()));
                    break;
                case PUT:
                    request = OkHttpUtil.buildPutRequest(url, headers, OkHttpUtil.buildRequestBody(step.getBodyType(), step.getBody(), step.getFormData()));
                    break;
                default:
                    log.error("{} http方法不被支持！", method);
                    return;
            }


            Response response = OkHttpUtil.call(request, step.getTimeout());

            StepResponseEntity stepResponse = OkHttpUtil.resolveResponse(response);
            log.info("Step :{}  result:{}", step.getId(), stepResponse);
            appendLog(pipelineContext, step, "Http Status Code : " + response.code());
            appendLog(pipelineContext, step, "Http Result : " + stepResponse.getResult());
            //设置结果
            getStepLog(pipelineContext, step).setStepResponse(stepResponse);
            if (response.code() < 200 || response.code() >= 400) {
                throw new ExecuteException(step.getName() + "调用失败！");
            }
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            handleExp(pipelineContext, step, e);
        }


    }

    private void handleExp(PipelineContext pipelineContext, HttpStepEntity step, Exception e) {
        log.error("Http调用失败！", e);
        createResponse(pipelineContext, step, step.getName() + "执行失败！原因：" + e.getMessage());
        throw new ExecuteException("Http 调用失败！" + e.getMessage(), e);
    }

    private void log(PipelineContext pipelineContext, HttpStepEntity step, String url, List<Pair<String, String>> headers) {
        appendLog(pipelineContext, step, "Http Method:" + step.getMethod());
        appendLog(pipelineContext, step, "Http Url:" + url);
        if (CollectionUtils.isNotEmpty(headers)) {
            appendLog(pipelineContext, step, "Http Headers : " + headers.stream().map(Pair::toKVString).collect(Collectors.joining(";")));
        }
        if (CollectionUtils.isNotEmpty(step.getCookies())) {
            appendLog(pipelineContext, step, "Http Cookies : " + step.getCookies().stream().map(Pair::toKVString).collect(Collectors.joining(";")));
        }
        if (StringUtils.isNotBlank(step.getCookieText())) {
            appendLog(pipelineContext, step, "Http Cookie : " + JSON.toJSONString(step.getCookieText()));
        }
        if (CollectionUtils.isNotEmpty(step.getUrlParams())) {
            appendLog(pipelineContext, step, "Http UrlParams : " + step.getUrlParams().stream().map(Pair::toKVString).collect(Collectors.joining(";")));
        }
        if (CollectionUtils.isNotEmpty(step.getFormData())) {
            appendLog(pipelineContext, step, "Http FormData : " + step.getFormData().stream().map(Pair::toKVString).collect(Collectors.joining(";")));
        }
        if (StringUtils.isNotBlank(step.getBody())) {
            appendLog(pipelineContext, step, "Http Body : " + step.getBody());
        }

    }

    @SuppressWarnings("unchecked")
    private List<Pair<String, String>> resolveHeaders(HttpStepEntity step) {
        ArrayList<Pair<String, String>> newHeaders = Lists.newArrayList(step.getHeaders() == null ? Collections.EMPTY_LIST : step.getHeaders());
        BodyType bodyType = step.getBodyType();
        if (bodyType != null && newHeaders.stream().noneMatch(pair -> "Content-Type".equalsIgnoreCase(pair.getName()))) {
            newHeaders.add(Pair.of("Content-Type", bodyType.getContentType()));
        }
        if (StringUtils.isNotBlank(step.getCookieText())) {
            newHeaders.add(Pair.of("Cookie", step.getCookieText()));
        }
        if (CollectionUtils.isNotEmpty(step.getCookies())) {
            String cookies = step.getCookies().stream()
                    .filter(pair -> StringUtils.isNotBlank(pair.getName()))
                    .map(Pair::toPairString).collect(Collectors.joining(";"));
            newHeaders.add(Pair.of("Cookie", cookies));
        }

        return newHeaders.stream()
                .filter(pair -> StringUtils.isNotBlank(pair.getName()))
                .collect(Collectors.toList());
    }

    private String getUrl(String url, List<Pair<String, String>> urlParams) {

        String finalUrl = joinUrlParams(url, urlParams);
        try {
            new URL(finalUrl);
        } catch (MalformedURLException e) {
            throw new ExecuteException(finalUrl + "不是有效的URL！");
        }
        return finalUrl;
    }

    /**
     * 拼接url params
     */
    private String joinUrlParams(String url, List<Pair<String, String>> urlParams) {
        if (CollectionUtils.isEmpty(urlParams)) {
            return url;
        }
        String query = urlParams.stream()
                .filter(pair -> StringUtils.isNotBlank(pair.getName()))
                .map(pair -> pair.getName() + "=" + pair.getValue())
                .collect(Collectors.joining("&"));


        if (url.contains("?") && url.contains("&")) {
            return url + "&" + query;
        } else {
            return url + "?" + query;
        }
    }

    private HttpStepEntity determineTemplate(HttpStepEntity httpStepEntity) {
        if (httpStepEntity.getUrlType() == UrlType.INPUT) {
            return httpStepEntity;
        }
        InstanceEntity instance = instanceCenter.getInstance(httpStepEntity.getInstanceId(), InstanceTypeEnum.HTTP);
        if (instance == null) {
            throw new ExecuteException(httpStepEntity.getName() + "使用的HTTP模板不存在！");
        }
        HttpStepEntity template = new HttpStepEntity();
        template.setBody(instance.getBody());
        template.setBodyType(instance.getBodyType());
        template.setCookies(instance.getCookies());
        template.setFormData(instance.getFormData());
        template.setCookieText(instance.getCookieText());
        template.setHeaders(instance.getHeaders());
        template.setMethod(instance.getMethod());
        template.setTimeout(instance.getTimeout());
        template.setUrl(instance.getUrl());
        template.setUrlParams(instance.getUrlParams());
        return template;
    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.HTTP;
    }


}
