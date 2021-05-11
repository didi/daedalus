package com.didichuxing.daedalus.common.dto.pipeline;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * pipeline info
 *
 * @author : jiangxinyu
 * @date : 2020/3/25
 */
@Data
public class PipelineInfo {

    @ApiModelProperty("流水线id")
    private String id;

    @ApiModelProperty("流水线名称")
    private String name;

    @ApiModelProperty("创建人")
    private String creator;
    @ApiModelProperty("创建人CN")
    private String creatorCN;

    @ApiModelProperty("负责人")
    private List<String> owner;

    @ApiModelProperty("负责人中文")
    private List<String> ownerCN;

    @ApiModelProperty("业务线")
    private Integer bizLine;

    @ApiModelProperty("标签")
    private List<String> tags;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("运行时间时间")
    private Date executeTime;

    @ApiModelProperty("执行次数")
    private Integer executeCount;

    @ApiModelProperty("是否收藏")
    private boolean collect;

    @ApiModelProperty("是否可编辑")
    private boolean editable;
}
