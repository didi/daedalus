package com.didichuxing.daedalus.entity.step.variables;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Data
public class VariableEntity {

    @Field
    private List<InputVarEntity> inputVars;

    @Field
    private List<GlobalVarEntity> globalVars;

    @Field
    private List<DynamicVarEntity> dynamicVars;

//    @Field
//    private List<EnvVarEntity> envVars;
}
