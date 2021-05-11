package com.didichuxing.daedalus.pojo.request;

import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.common.dto.instance.Instance;
import com.didichuxing.daedalus.common.dto.log.Log;
import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Data
@ApiModel(description = "流水线运行request")
public class ExecuteRequest  {

    @ApiModelProperty("pipelineId")
    @NotBlank(message = "流水线id不能为空")
    private String pipelineId;

    @ApiModelProperty("运行时用户输入")
    private Map<String, String> inputs;

    @ApiModelProperty("环境")
    private String env;

    /**
     * 运行类型
     */
    private ExecTypeEnum execType;

    private String resumeLogId;

    /**
     * **********************以下是线下运行需要的数据*******************************
     */


    private Map<String, EnvGroup> envGroups = new HashMap<>();

    private Map<String, Instance> instances = new HashMap<>();


    /**
     * key ：pipeline id
     */
    private Map<String, Pipeline> pipelines = new HashMap<>();

    /**
     * 线下resume恢复
     */
    private Log log;

    /**
     *
     */
    @ApiModelProperty("debug的流水线")
    private Pipeline pipeline;

}
