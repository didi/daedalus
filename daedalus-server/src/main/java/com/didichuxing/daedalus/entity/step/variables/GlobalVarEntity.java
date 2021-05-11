package com.didichuxing.daedalus.entity.step.variables;

import com.didichuxing.daedalus.common.enums.ValueTypeEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
public class GlobalVarEntity {
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


    /**
     * 变量值
     */
    @Field
    private String value;


    /**
     * 变量值类型，字符串/数字/布尔
     */
    @Field
    private ValueTypeEnum valueType;

    /**
     * 备注
     */
    @Field
    private String remark;
}
