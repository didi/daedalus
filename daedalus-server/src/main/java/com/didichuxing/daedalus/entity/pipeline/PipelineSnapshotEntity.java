package com.didichuxing.daedalus.entity.pipeline;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * @author : jiangxinyu
 * @date : 2020/9/17
 */
@Data
@Document("pipelineSnapshot")
public class PipelineSnapshotEntity {

    @Id
    private String id;

    @Field
    private PipelineEntity pipeline;

    @Field
    @CreatedDate
    private Date createTime;

    @Field
    private String operator;
}
