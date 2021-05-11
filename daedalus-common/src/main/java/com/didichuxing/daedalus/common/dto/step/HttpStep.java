package com.didichuxing.daedalus.common.dto.step;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.didichuxing.daedalus.common.enums.http.UrlType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class HttpStep extends BaseStep {

    @NotNull(message = "urlType不能为空！")
    private UrlType urlType;

    private MethodType method;
    private String url;
    private Integer timeout;
    private String instanceId;
    private BodyType bodyType;
    private String body;
    @Valid
    private List<Pair<String, String>> formData;
    @Valid
    private List<Pair<String, String>> headers;
    @Valid
    private List<Pair<String, String>> cookies;
    @Valid
    private List<Pair<String, String>> urlParams;
    private String cookieText;
    private Boolean followRedirect;

}
