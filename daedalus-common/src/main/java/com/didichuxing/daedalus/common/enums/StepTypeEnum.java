package com.didichuxing.daedalus.common.enums;

import com.didichuxing.daedalus.common.dto.step.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */

@Getter
@AllArgsConstructor
public enum StepTypeEnum {
    EMPTY(10000, "空白", HttpStep.class),
    HTTP(20001, "HTTP", HttpStep.class),
    DUBBO(20002, "Dubbo", DubboStep.class),
    MYSQL(30001, "MYSQL", MysqlStep.class),
    REDIS(30002, "REDIS", RedisStep.class),
    ES(30005, "ES", ESStep.class),
    GROOVY(40001, "GROOVY", GroovyStep.class),
    IMPORT(50001, "IMPORT", GroovyStep.class),
    NOTICE(60001, "NOTICE", GroovyStep.class),
    ;


    private int code;
    private String desc;
    private Class<? extends BaseStep> stepClass;


    public static StepTypeEnum getEnum(String stepType) {
        for (StepTypeEnum stepTypeEnum : StepTypeEnum.values()) {
            if (stepTypeEnum.name().equalsIgnoreCase(stepType)) {
                return stepTypeEnum;
            }
        }
        return null;
    }
}
