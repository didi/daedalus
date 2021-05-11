package com.didichuxing.daedalus.common.dto.step;

import com.didichuxing.daedalus.common.dto.step.additional.Condition;
import com.didichuxing.daedalus.common.dto.step.variables.ExtractVar;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */
@Setter
@Getter
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "stepType",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(name = "HTTP", value = HttpStep.class),
        @JsonSubTypes.Type(name = "EMPTY", value = EmptyStep.class),
        @JsonSubTypes.Type(name = "DUBBO", value = DubboStep.class),
        @JsonSubTypes.Type(name = "MYSQL", value = MysqlStep.class),
        @JsonSubTypes.Type(name = "REDIS", value = RedisStep.class),
        @JsonSubTypes.Type(name = "GROOVY", value = GroovyStep.class),
        @JsonSubTypes.Type(name = "IMPORT", value = ImportStep.class),
        @JsonSubTypes.Type(name = "NOTICE", value = NoticeStep.class),
        @JsonSubTypes.Type(name = "ES", value = ESStep.class)
})
public abstract class BaseStep {

    @ApiModelProperty(value = "step唯一id")
    @NotBlank(message = "step id不能为空")
    private String id;

    @NotBlank(message = "流水线名称不能为空！")
    private String name;

    private Condition condition;

    private String remark;

    @NotNull(message = "流水线类型不能为空！")
    private StepTypeEnum stepType;

    @ApiModelProperty(value = "延迟执行时间,单位毫秒", example = "10")
    private Integer delay;

    private String alias;

    private String preStepScript;

    private String postStepScript;

    private List<ExtractVar> extractVars;

    private List<Assert> asserts;

    private Boolean output;
}
