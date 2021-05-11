package com.didichuxing.daedalus.common.dto.log;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Setter
@Getter
@ToString
public class StepResponse {

    /**
     * http response header
     */
    private Map<String, List<String>> headers;

    /**
     * 通用result
     */
    private String result;

    /**
     * dubbo attachment
     */
    private Map<String, String> attachments;

}
