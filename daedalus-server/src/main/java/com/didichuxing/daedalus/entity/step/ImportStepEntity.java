package com.didichuxing.daedalus.entity.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/10/23
 */
@Setter
@Getter
@ToString(callSuper = true)
public class ImportStepEntity extends BaseStepEntity {

    @Field
    private String pipelineId;

    @Field
    private String env;

    @Field
    private Map<String, String> inputs;
}
