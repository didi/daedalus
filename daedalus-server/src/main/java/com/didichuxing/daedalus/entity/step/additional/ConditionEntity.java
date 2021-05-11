package com.didichuxing.daedalus.entity.step.additional;

import com.didichuxing.daedalus.common.enums.LogicTypeEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Data
public class ConditionEntity {
    @Field
    private LogicTypeEnum logicType;
    @Field
    private List<RuleEntity> rules;
}
