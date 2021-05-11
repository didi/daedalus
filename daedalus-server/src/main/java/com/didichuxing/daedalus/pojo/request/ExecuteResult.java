package com.didichuxing.daedalus.pojo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Data
@ApiModel(description = "流水线运行result")
public class ExecuteResult implements Serializable {

    @ApiModelProperty("pipelineId")
    @NotBlank(message = "流水线id不能为空")
    private String pipelineId;

    @ApiModelProperty("运行时用户输入")
    private Map<String, String> inputs;

    @ApiModelProperty("环境")
    private String env;

    @ApiModelProperty("是否成功")
    private boolean success;

    @ApiModelProperty("结果")
    private String result;
    private String logId;
    private String exceptionMsg;


}
