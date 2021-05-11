package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/10/26
 */
@AllArgsConstructor
@Getter
public enum ScheduleStatusEnum {
    ENABLED("启用"),
    DISABLED("禁用"),
    RUNNING("运行中"),
    DELETED("已删除");


    private String desc;
}
