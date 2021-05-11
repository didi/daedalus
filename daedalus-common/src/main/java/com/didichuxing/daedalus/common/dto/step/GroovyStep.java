package com.didichuxing.daedalus.common.dto.step;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class GroovyStep extends BaseStep {

    @ApiModelProperty("groovy脚本")
    private String script;


}
