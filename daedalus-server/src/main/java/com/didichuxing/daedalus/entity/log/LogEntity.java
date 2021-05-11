package com.didichuxing.daedalus.entity.log;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.common.enums.ExecuteStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/26
 */
@Setter
@Getter
@ToString
@Document("log")
public class LogEntity {

    @Id
    private String id;
    @Field
    @Indexed
    private String pipelineId;

    @Field
    private Map<String, String> inputs;

    @Field
    private String username;

    @Field
    private String usernameCN;

    @Field
    @CreatedDate
    @Indexed
    private Date createTime;

    @Field
    private List<StepLogEntity> stepLogs = new LinkedList<>();

    /**
     * 流水线运行最后结果
     */
    @Field
    private String result;//todo

    @Field
    private ExecuteStatusEnum executeStatus;


    /**
     * 运行时变量
     */
    @Field
    private Map<String, String> runtimeVars;

    @Field
    private ClusterEnum cluster;

    @Field
    private ExecTypeEnum execType;
}
