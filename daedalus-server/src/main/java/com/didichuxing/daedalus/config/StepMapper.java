package com.didichuxing.daedalus.config;

import com.didichuxing.daedalus.common.dto.step.*;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.*;
import lombok.AllArgsConstructor;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */


@AllArgsConstructor
public enum StepMapper {

    EMPTY(StepTypeEnum.EMPTY, EmptyStep.class, EmptyStepEntity.class),
    HTTP(StepTypeEnum.HTTP, HttpStep.class, HttpStepEntity.class),
    DUBBO(StepTypeEnum.DUBBO, DubboStep.class, DubboStepEntity.class),
    MYSQL(StepTypeEnum.MYSQL, MysqlStep.class, MysqlStepEntity.class),
    REDIS(StepTypeEnum.REDIS, RedisStep.class, RedisStepEntity.class),
    ES(StepTypeEnum.ES, ESStep.class, ESStepEntity.class),
    GROOVY(StepTypeEnum.GROOVY, GroovyStep.class, GroovyStepEntity.class),
    IMPORT(StepTypeEnum.IMPORT, ImportStep.class, ImportStepEntity.class),
    NOTICE(StepTypeEnum.NOTICE, NoticeStep.class, NoticeStepEntity.class),
    ;

    public StepTypeEnum stepType;
    public Class<? extends BaseStep> dtoClass;
    public Class<? extends BaseStepEntity> entityClass;

    public static Class<? extends BaseStep> getDtoClass(StepTypeEnum stepType) {
        return StepMapper.valueOf(stepType.name()).dtoClass;
    }

    public static Class<? extends BaseStepEntity> getEntityClass(StepTypeEnum stepType) {
        return StepMapper.valueOf(stepType.name()).entityClass;
    }


}
