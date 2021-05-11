package com.didichuxing.daedalus.entity.pipeline;

import com.didichuxing.daedalus.common.enums.StatusEnum;
import com.didichuxing.daedalus.entity.step.variables.VariableEntity;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */
@Data
@Document("pipeline")
public class PipelineEntity {

    @Id
    private String id;

    @Field
    @Indexed
    private StatusEnum status = StatusEnum.OK;

    @Field
    private String name;

    @Field
    private boolean online;

//    @Field
//    private List<String> owner;

    @Field
    @Indexed
    private String creator;

    @Field
    private String creatorCN;

    @Field
    private Integer bizLine;

    @Field
    private PermissionEntity permission;

    @Field
    private boolean envSupport;

    @Field
    private String envGroupId;

    @Field
    private List<String> tags;

    @Field
    private String remark;

    @Field
    @CreatedDate
    private Date createTime;

    @Field
    @LastModifiedDate
    private Date updateTime;

    @Field
    private VariableEntity variable;

    @Field
    private FlowEntity flow;
}
