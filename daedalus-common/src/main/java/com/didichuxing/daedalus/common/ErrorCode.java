package com.didichuxing.daedalus.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    FAILED(-1, "运行失败！"),
    SUCCESS(0, "操作成功！"),
    STEP_TYPE_MISS(10000, "缺少stepType参数"),
    UNKNOWN_STEP(10001, "未知类型STEP"),
    MISS_PARAM(10002, "参数缺失"),
    ERROR_PARAM(10003, "参数错误"),
    PIPELINE_NOT_FOUND(10004, "流水线不存在！"),
    PERMISSION_DEFINED(10005, "权限不足！"),
    EMPTY_FLOW(10006, "流水线步骤为空,请先编辑流水线添加Step！"),
    EMPTY_ENV(10007, "请选择运行环境！"),
    UNKNOWN_EXEC_TYPE(10008, "未知运行类型"),
    ;

    private int code;
    private String desc;
}
