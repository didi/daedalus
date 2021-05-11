package com.didichuxing.daedalus.entity.step;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.didichuxing.daedalus.common.enums.http.UrlType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class HttpStepEntity extends BaseStepEntity {


    @Field
    private UrlType urlType;
    @Field
    private MethodType method;
    @Field
    private String url;
    @Field
    private Integer timeout;
    @Field
    private String instanceId;
    @Field
    private BodyType bodyType;
    @Field
    private String body;
    @Field
    private List<Pair<String, String>> formData;
    @Field
    private List<Pair<String, String>> headers;
    @Field
    private List<Pair<String, String>> cookies;
    @Field
    private List<Pair<String, String>> urlParams;
    @Field
    private String cookieText;
    @Field
    private Boolean followRedirect;

}
