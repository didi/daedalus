package com.didichuxing.daedalus.util;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.step.HttpStep;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.didichuxing.daedalus.common.enums.http.UrlType;
import com.didichuxing.daedalus.util.curl.BasicCurlParser;
import lombok.experimental.UtilityClass;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/8/19
 */
@UtilityClass
public class CurlUtil {


    public static HttpStep parse(String curlCommand) {
        BasicCurlParser.Request request = new BasicCurlParser().parse(curlCommand);

        HttpStep httpStep = new HttpStep();

        httpStep.setUrl(request.getUrlNoQuery());
        httpStep.setUrlType(UrlType.SELECT);

        httpStep.setHeaders(request.getHeaders().entrySet().stream().map(header -> Pair.of(header.getKey(), header.getValue())).collect(Collectors.toList()));
        httpStep.setBodyType(BodyType.get(request.getHeaders().get("Content-Type")));
        httpStep.setBody(request.getPostData());//todo  put?
        httpStep.setMethod(MethodType.parse(request.getMethod()));//todo method 对嘛
        httpStep.setUrlParams(request.getUrlParams().entrySet().stream().map(header -> Pair.of(header.getKey(), header.getValue())).collect(Collectors.toList()));
        httpStep.setCookies(request.getCookieInHeaders().stream().map(cookie -> Pair.of(cookie.getName(), cookie.getValue())).collect(Collectors.toList()));

        httpStep.setFormData(request.getFormData().entrySet().stream().map(formData -> Pair.of(formData.getKey(), formData.getValue())).collect(Collectors.toList()));
        httpStep.setStepType(StepTypeEnum.HTTP);
        httpStep.setTimeout(10000);

        return httpStep;

    }




}
