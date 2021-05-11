package com.didichuxing.daedalus.common.dto.step;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/10/23
 */
@Setter
@Getter
@ToString(callSuper = true)
public class ImportStep extends BaseStep {

    private String pipelineId;

    private String env;

    private Map<String, String> inputs;
}
