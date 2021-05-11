package com.didichuxing.daedalus.common.enums;

import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Getter
public enum BizLineEnum {
    DEFAULT(0, "通用"),
    ;

    BizLineEnum(int code, String bizName) {
        this.code = code;
        this.bizName = bizName;
    }
    private String bizName;
}
