package com.didichuxing.daedalus.common.dto.pipeline;

import com.didichuxing.daedalus.common.dto.step.variables.Variable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */
@Data
@ApiModel("流水线")
public class Pipeline {

    @ApiModelProperty("流水线id")
    private String id;

    @ApiModelProperty("流水线名称")
    @NotBlank(message = "权限不能为空！")
    private String name;

    @ApiModelProperty("线上机房")
    private boolean online;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建人中文")
    private String creatorCN;

//    @ApiModelProperty("负责人")
//    private List<String> owner;

    @ApiModelProperty("业务线")
    private Integer bizLine;

    @ApiModelProperty("权限")
    @NotNull(message = "权限不能为空！")
    private Permission permission;

    @ApiModelProperty("多环境支持")
    private boolean envSupport;

    @ApiModelProperty("关联环境组id")
    private String envGroupId;

    @ApiModelProperty("标签")
    private List<String> tags;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty(example = "2020-01-01 10:10:10", hidden = true)
    @Null(message = "createTime需为空！")
    private Date createTime;

    @ApiModelProperty(example = "2020-01-01 10:10:10", hidden = true)
    @Null(message = "updateTime需为空！")
    private Date updateTime;

    @ApiModelProperty("变量")
    @Valid
    private Variable variable;

    @ApiModelProperty("步骤")
    @Valid
    private Flow flow;

    @ApiModelProperty("是否收藏")
    private boolean collect;
}
