package com.didichuxing.daedalus.common.dto.step;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.step.additional.DubboParam;
import com.didichuxing.daedalus.common.enums.dubbo.DubboType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class DubboStep extends BaseStep {

    @ApiModelProperty("dubbo类型,REGISTER/DIRECT")
    @NotNull
    private DubboType dubboType;

    private String register;//todo
    private String serviceJson;
    private String ip;
    private Integer port;
    @NotBlank(message = "Dubbo接口不能为空")
    private String className;
    private String group;
    private String version;
    @NotBlank(message = "Dubbo 方法不能为空")
    private String method;
    @Valid
    private List<DubboParam<String, String>> params;
    @Valid
    private List<Pair<String, Object>> attachments;

}
