package com.didichuxing.daedalus.entity.step.variables;

import com.didichuxing.daedalus.common.enums.ExtractLocEnum;
import com.didichuxing.daedalus.common.enums.ValueTypeEnum;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * 提取变量
 *
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
public class ExtractVarEntity {
    /**
     * 变量提取位置
     */
    @Field
    private ExtractLocEnum location;
    @Field
    private String name;
    @Field
    private String path;
    @Field
    private ValueTypeEnum valueType;


    public boolean valid() {
        return location != null && StringUtils.isNotBlank(name);
    }
}
