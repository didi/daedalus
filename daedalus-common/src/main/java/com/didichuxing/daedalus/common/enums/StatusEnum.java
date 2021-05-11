package com.didichuxing.daedalus.common.enums;

import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/23
 */
@Getter
public enum StatusEnum {
    OK("正常"),
    DELETED("已删除");

    StatusEnum(String desc) {
        this.desc = desc;
    }

    private String desc;
}
