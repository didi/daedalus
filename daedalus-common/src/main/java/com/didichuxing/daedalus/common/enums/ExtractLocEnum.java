package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Getter
@AllArgsConstructor
public enum ExtractLocEnum {
    RESULT("result", "Step运行结果"),
    HTTP_HEADER("header", "Http Response"),
    ATTACHMENT("attachment", "Dubbo Attachment");


    private String loc;
    private String desc;
}
