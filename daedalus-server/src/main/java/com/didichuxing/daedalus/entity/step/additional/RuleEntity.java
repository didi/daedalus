package com.didichuxing.daedalus.entity.step.additional;

import com.didichuxing.daedalus.common.enums.OperatorEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author : jiangxinyu
 * @date : 2020/4/24
 */
@Data
public class RuleEntity {
    /**
     * 使用的参数变量
     */
    @Field
    private String variable;

    @Field
    private String value;

    @Field
    private OperatorEnum operator;

    @Field
    private String regex;


}
