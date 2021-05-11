package com.didichuxing.daedalus.common.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : jiangxinyu
 * @date : 2020/4/23
 */
@Data
@AllArgsConstructor
public class BizLine {

    @ApiModelProperty("业务线代码")
    private int code;

    @ApiModelProperty("业务线名称")
    private String name;
}
