package com.didichuxing.daedalus.common.dto.step.additional;

import com.didichuxing.daedalus.common.enums.OperatorEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author : jiangxinyu
 * @date : 2020/4/24
 */
@Data
public class Rule {
    /**
     * 使用的参数变量
     */
    @NotNull(message = "条件不能为空！")
    private String variable;

    /**
     * 可能是值  可能是变量
     */
    @NotNull(message = "条件不能为空！")
    private String value;

    @NotNull(message = "条件不能为空！")
    private OperatorEnum operator;

    private String regex;
}
