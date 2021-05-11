package com.didichuxing.daedalus.entity.log;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Setter
@Getter
@ToString
public class StepResponseEntity {

    /**
     * http response header
     */
    @Field
    private Map<String, List<String>> headers;

    /**
     * 通用result
     */
    @Field
    private String result;

    /**
     * dubbo attachment
     */
    @Field
    private Map<String, String> attachments;

}
