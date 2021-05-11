package com.didichuxing.daedalus.entity.schedule;

import com.didichuxing.daedalus.common.enums.ScheduleStatusEnum;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/10/23
 */
@Data
@Document("schedule")
public class ScheduleEntity {

    @Id
    private String id;

    @Field
    private String name;

    @Field
    private String cronRule;

    @Field
    private ScheduleStatusEnum status = ScheduleStatusEnum.DISABLED;

    @Field
    private String pipelineId;

    @Field
    private Map<String, String> inputs;

    @Field
    private String env;

    @Field
    private String creator;

    @Field
    private String creatorCN;

    @Field
    @CreatedDate
    private Date createTime;

    @Field
    @LastModifiedDate
    private Date updateTime;
}
