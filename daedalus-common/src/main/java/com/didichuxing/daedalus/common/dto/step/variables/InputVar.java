package com.didichuxing.daedalus.common.dto.step.variables;

import com.didichuxing.daedalus.common.enums.DateFormatEnum;
import com.didichuxing.daedalus.common.enums.InputTypeEnum;
import com.didichuxing.daedalus.common.enums.ValueTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 输入变量
 *
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
@ApiModel("用户输入变量")
public class InputVar {


    /**
     * 变量展示名
     */
    @ApiModelProperty("表单输入Label")
    private String label;

    /**
     * 变量名
     */
    @ApiModelProperty("变量名")
    private String name;

    @ApiModelProperty("变量输入类型")
    private InputTypeEnum inputType;

    /**
     * 变量值类型，字符串/数字/布尔
     */
    @ApiModelProperty("变量值类型，字符串/数字/布尔")
    private ValueTypeEnum valueType;


    @ApiModelProperty("单选、多选、下拉框的选项")
    private List<Option> options;

    @ApiModelProperty("是否必填")
    private boolean required = true;//todo


    @ApiModelProperty("日期格式")
    private DateFormatEnum dateFormat;
    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;


    @ApiModelProperty("默认值")
    private String defaultValue;

    /**
     * 依赖的输入变量名
     */
    private String dependencyInputName;

    /**
     * 组件依赖的选项，显示或隐藏
     */
    private List<String> dependencyOptions;

    /**
     * 组件选项依赖的选项
     */
    private List<OptionRelation> optionRelations;

}
