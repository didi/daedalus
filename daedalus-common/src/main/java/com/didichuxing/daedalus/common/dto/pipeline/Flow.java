package com.didichuxing.daedalus.common.dto.pipeline;

import com.didichuxing.daedalus.common.dto.step.BaseStep;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Setter
@Getter
@ToString
public class Flow {

    @ApiModelProperty("步骤")
    @Valid
    private List<BaseStep> steps;

    @ApiModelProperty("连线")
    private List<Edge> edges;
}
