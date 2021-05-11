package com.didichuxing.daedalus.entity.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class MysqlStepEntity extends BaseStepEntity {

    @Field
    private String instanceId;
    @Field
    private String sql;

}
