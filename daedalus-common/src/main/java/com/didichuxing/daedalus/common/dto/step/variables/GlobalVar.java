package com.didichuxing.daedalus.common.dto.step.variables;

import com.didichuxing.daedalus.common.enums.ValueTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
@ApiModel("全局变量")
public class GlobalVar {

    /**
     * 变量展示名
     */
    @ApiModelProperty("变量展示名")
    private String label;

    /**
     * 变量名
     */
    @ApiModelProperty("变量展示名")
    private String name;


    /**
     * 变量值
     */
    @ApiModelProperty("变量值")
    private String value;


    /**
     * 变量值类型，字符串/数字/布尔
     */
    @ApiModelProperty("变量值类型,STRING/BOOLEAN/NUMBER")
    private ValueTypeEnum valueType;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

}
