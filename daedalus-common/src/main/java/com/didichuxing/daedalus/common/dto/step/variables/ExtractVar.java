package com.didichuxing.daedalus.common.dto.step.variables;

import com.didichuxing.daedalus.common.enums.ExtractLocEnum;
import com.didichuxing.daedalus.common.serializer.VirtualIdWriter;
import com.fasterxml.jackson.databind.annotation.JsonAppend;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 提取变量
 *
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Data
@ApiModel("提取变量")
@JsonAppend(props = {@JsonAppend.Prop(name = "id", value = VirtualIdWriter.class)})
public class ExtractVar {
    /**
     * 变量提取位置
     */
    @ApiModelProperty("变量提取点，可选ATTACHMENT，HTTP_HEADER,RESULT")
    private ExtractLocEnum location;

    @ApiModelProperty("提取变量名")
    private String name;

    @ApiModelProperty("变量提取的路径")
    private String path;
}
