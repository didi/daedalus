package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 前端输入形式
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@AllArgsConstructor
@Getter
public enum InputTypeEnum {
    INPUT("输入框"),
    NUMBER_INPUT("数字输入框"),
    TEXTAREA("文本框"),
    SELECT("下拉框"),
    RADIO("单选框"),
    CHECKBOX("多选框"),
    SWITCH("开关"),
    DATE_PICKER("日期选择器"),
    DATE_TIME_PICKER("日期时间选择器"),
    TIME_PICKER("时间选择器");

    private String desc;
}
