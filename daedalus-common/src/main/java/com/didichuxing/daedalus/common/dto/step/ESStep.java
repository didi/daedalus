package com.didichuxing.daedalus.common.dto.step;

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
public class ESStep extends BaseStep {
    /**
     * es实例id
     */
    private String instanceId;
    private String queryType;
    private String sql;
    private String dsl;


}
