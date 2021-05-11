package com.didichuxing.daedalus.entity.schedule;

import com.didichuxing.daedalus.common.enums.TaskStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/10/26
 */
@Data
@Document("task")
public class TaskEntity {


    @Id
    private String id;

    @Field
    @Indexed
    private String scheduleId;

    @Field
    @Indexed
    private String pipelineId;

    @Field
    @Indexed
    private TaskStatusEnum status;

    @Field
    private String env;

    @Field
    private Map<String, String> inputs;

    @Field
    private Date executeTime;

    @Field
    private String logId;

    @Field
    @CreatedDate
    private Date createTime;

    @Field
    @LastModifiedDate
    private Date updateTime;
}
