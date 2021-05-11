package com.didichuxing.daedalus.common.dto.log;

import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.common.enums.ExecuteStatusEnum;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/26
 */
@Data
public class Log {
    private String id;

    private String pipelineId;

    private Map<String, String> inputs;

    private String username;

    private String usernameCN;


    private Date createTime;

    private List<StepLog> stepLogs;

    private String result;

    private ExecuteStatusEnum executeStatus;

    private Map<String, String> runtimeVars;

    private ClusterEnum cluster;
}
