package com.didichuxing.daedalus.common.dto.instance;

import com.didichuxing.daedalus.common.DesensitizeConverter;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Data
public class Instance {

    private String id;

    @NotBlank(message = "名称不能为空！")
    private String name;

    @NotNull(message = "实例类型不能为空！")
    private InstanceTypeEnum instanceType;

    @ApiModelProperty(example = "2020-01-01 10:10:10", hidden = true)
    @Null(message = "createTime需为空！")
    private Date createTime;

    @ApiModelProperty(example = "2020-01-01 10:10:10", hidden = true)
    @Null(message = "updateTime需为空！")
    private Date updateTime;

    private String creator;

    /**
     * 实例ip
     */
    private String ip;

    private Integer port;

    /**
     * 接口路径
     */
//    private String path;

    private String username;

    @JsonSerialize(converter = DesensitizeConverter.class)
    private String password;

    @ApiModelProperty("mysql数据库名")
    private String database;

    @ApiModelProperty("注册中心协议类型,如ZK")
    private String protocol;
    private String remark;


    //http的

    private MethodType method;
    private String url;
    private Integer timeout;
    private BodyType bodyType;
    private String body;
    private List<Pair<String, String>> formData;
    private List<Pair<String, String>> headers;
    private List<Pair<String, String>> cookies;
    private List<Pair<String, String>> urlParams;
    private String cookieText;
}
