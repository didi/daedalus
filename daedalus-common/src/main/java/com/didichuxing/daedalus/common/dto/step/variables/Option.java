package com.didichuxing.daedalus.common.dto.step.variables;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Data
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public class Option {

    @ApiModelProperty("输入时显示名")
    private String display;

    @ApiModelProperty("选择后实际值")
    private String value;

    @ApiModelProperty("option关联额外变量")
    private List<Pair<String, String>> extraVars;
}
