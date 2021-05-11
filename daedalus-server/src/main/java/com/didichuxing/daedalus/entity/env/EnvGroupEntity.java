package com.didichuxing.daedalus.entity.env;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Document("env")
@Setter
@Getter
@ToString
public class EnvGroupEntity {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    private String creator;

    @Field
    private String creatorCN;


    @Field
    private Integer bizLine;

    @Field
    @CreatedDate
    private Date createTime;

    @Field
    @LastModifiedDate
    private Date updateTime;

    @Field
    private StatusEnum status = StatusEnum.OK;

    /**
     * 存放行数据
     * 第一个是变量名 key:envVarName
     * 第二个是变量描述 key:envVarDesc
     * 后面的是 不同环境的变量值
     */
    @Field
    private List<LinkedHashMap<String, String>> data;

    @Field
    private Map<String, ClusterEnum> clusterInfo;

}
