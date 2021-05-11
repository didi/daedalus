package com.didichuxing.daedalus.entity.step.variables;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2021/1/14
 */
@Data
public class OptionRelationEntity {

    @Field
    private List<String> targetOptions;

    @Field
    private List<String> showOnOptions;

}
