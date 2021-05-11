package com.didichuxing.daedalus.entity.step.variables;

import com.didichuxing.daedalus.common.dto.Pair;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Data
public class OptionEntity {


    @Field
    private String display;

    @Field
    private String value;

    @Field
    private List<Pair<String, String>> extraVars;

}
