package com.didichuxing.daedalus.common.enums.permission;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@Getter
@AllArgsConstructor
public enum EditableEnum {
    ALL(0),
    OWNER(1),
    SCOPE(2),
    ;
    private int code;
}
