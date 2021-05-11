package com.didichuxing.daedalus.common.dto.step.variables;

import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import lombok.Data;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2021/1/14
 */
@Data
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public class OptionRelation {

    /**
     * 选项的真实值
     */
    private List<String> targetOptions;

    private List<String> showOnOptions;

}
