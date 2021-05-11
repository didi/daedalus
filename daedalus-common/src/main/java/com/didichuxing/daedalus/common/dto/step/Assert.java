package com.didichuxing.daedalus.common.dto.step;

import com.didichuxing.daedalus.common.enums.ExtractLocEnum;
import com.didichuxing.daedalus.common.enums.LogicMatcher;
import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Data;

/**
 * @author : jiangxinyu
 * @date : 2021/1/7
 */
@Data
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public class Assert {

    private ExtractLocEnum location = ExtractLocEnum.RESULT;

    private String path;

    private String var;

    private LogicMatcher logicMatcher;

    private String expectValue;

    private String message;
}
