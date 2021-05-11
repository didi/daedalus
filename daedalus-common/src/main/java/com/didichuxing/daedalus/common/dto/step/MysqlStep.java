package com.didichuxing.daedalus.common.dto.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class MysqlStep extends BaseStep {

    @NotBlank(message = "实例不能为空！")
    private String instanceId;

    @NotBlank(message = "SQL不能为空！")
    private String sql;

}
