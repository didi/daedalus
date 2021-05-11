package com.didichuxing.daedalus.common.dto.step.variables;

import lombok.Data;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Data
public class Variable {
    private List<InputVar> inputVars;

    private List<GlobalVar> globalVars;

    private List<DynamicVar> dynamicVars;

//    private List<EnvVar> envVars;
}
