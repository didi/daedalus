package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/26
 */
@Getter
@AllArgsConstructor
public enum OperatorEnum {

    IS("是", "=="),//字符串相等
    IS_NOT("不是", "!="),//字符串不相同
    EQUAL("等于", "=="),
    NOT_EQUAL("不等于", "!="),
    GRATER_THAN("大于", ">"),
    LESS_THAN("小于", "<"),
    GREATER_THAN_OR_EQUAL("大于等于", ">="),
    LESS_THAN_OR_EQUAL("小于等于", "<="),
    BEFORE("早于", "<"),
    AFTER("晚于", ">"),
    REGEX("符合正则", "=~");

    private String desc;
    private String operator;


}
