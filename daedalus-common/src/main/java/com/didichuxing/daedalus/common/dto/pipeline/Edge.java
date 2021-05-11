package com.didichuxing.daedalus.common.dto.pipeline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 连线
 *
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Data
public class Edge {

    @ApiModelProperty("源step id")
    @NotBlank(message = "edge source不能为空")
    private String source;

    @ApiModelProperty("目标step id")
    @NotBlank(message = "edge target不能为空")
    private String target;

    /**
     * 连线文字
     */
    @ApiModelProperty("连线文字")
    private String label;
}
