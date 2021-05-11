package com.didichuxing.daedalus.entity.step.variables;

import com.didichuxing.daedalus.common.enums.DateFormatEnum;
import com.didichuxing.daedalus.common.enums.InputTypeEnum;
import com.didichuxing.daedalus.common.enums.ValueTypeEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * 输入变量
 *
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
public class InputVarEntity {

    /**
     * 变量展示名
     */
    @Field
    private String label;

    /**
     * 变量名
     */
    @Field
    private String name;

    @Field
    private InputTypeEnum inputType;

    /**
     * 变量值类型，字符串/数字/布尔
     */
    @Field
    private ValueTypeEnum valueType;

    @Field
    private List<OptionEntity> options;

    @Field
    private boolean required = true;

    @Field
    private DateFormatEnum dateFormat;

    /**
     * 备注
     */
    @Field
    private String remark;

    @Field
    private String defaultValue;

    /**
     * 依赖的输入变量名
     */
    @Field
    private String dependencyInputName;

    /**
     * 依赖的选项
     */
    @Field
    private List<String> dependencyOptions;

    @Field
    private List<OptionRelationEntity> optionRelations;

}
