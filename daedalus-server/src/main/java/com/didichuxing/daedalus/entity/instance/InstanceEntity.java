package com.didichuxing.daedalus.entity.instance;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.common.enums.StatusEnum;
import com.didichuxing.daedalus.common.enums.http.BodyType;
import com.didichuxing.daedalus.common.enums.http.MethodType;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Data
@Document("instance")
public class InstanceEntity {

    @Id
    private String id;

    @Field
    private String name;
    @Field
    private InstanceTypeEnum instanceType;

    @CreatedDate
    @Field
    private Date createTime;

    @Field
    @LastModifiedDate
    private Date updateTime;

    @Field
    private String creator;
    @Field
    private String creatorCN;
    @Field
    private String ip;
    @Field
    private Integer port;


    @Field
    private String username;
    @Field
    private String password;


    @Field
    private String database;

    @Field
    private String protocol;

    @Field
    private String remark;

    @Field
    private StatusEnum status = StatusEnum.OK;

    //http的
    @Field
    private MethodType method;
    @Field
    private String url;
    @Field
    private Integer timeout;
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
}
