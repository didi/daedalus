package com.didichuxing.daedalus.common.dto.step.additional;

import com.didichuxing.daedalus.common.enums.LogicTypeEnum;
import lombok.Data;

import java.util.List;

/**
 * IF条件分支条件
 *
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Data
public class Condition {

    private LogicTypeEnum logicType;

    private List<Rule> rules;

}
