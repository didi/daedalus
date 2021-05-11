package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/10/26
 */
@AllArgsConstructor
@Getter
public enum TaskStatusEnum {
    INITED("待运行"),
    RUNNING("运行中"),
    END("运行结束"),
    INVALID("废弃");


    private String desc;
}
