package com.didichuxing.daedalus.common.dto.schedule;

import com.didichuxing.daedalus.common.enums.ScheduleStatusEnum;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/10/23
 */
@Data
public class Schedule {

    private String id;

    private String name;

    @NotBlank
    private String pipelineId;

    @NotBlank
    private String cronRule;

    private String env;

    private String creator;

    private String creatorCN;

    private Date createTime;

    private ScheduleStatusEnum status = ScheduleStatusEnum.DISABLED;

    private Map<String, String> inputs;
}
