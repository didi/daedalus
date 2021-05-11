package com.didichuxing.daedalus.entity.step.additional;

import com.didichuxing.daedalus.common.enums.LogicMatcher;
import com.didichuxing.daedalus.common.enums.ExtractLocEnum;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author : jiangxinyu
 * @date : 2020/11/12
 */
@Data
public class AssertEntity {

    @Field
    private ExtractLocEnum location = ExtractLocEnum.RESULT;

    @Field
    private String path;

    @Field
    private String var;

    @Field
    private LogicMatcher logicMatcher;

    @Field
    private String expectValue;

    @Field
    private String message;
}
