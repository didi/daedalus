package com.didichuxing.daedalus.entity.step;

import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.step.additional.DubboParam;
import com.didichuxing.daedalus.common.enums.dubbo.DubboType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/3/21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class DubboStepEntity extends BaseStepEntity {
    @Field
    private DubboType dubboType;
    @Field
    private String register;
    @Field
    private String serviceJson;
    @Field
    private String ip;
    @Field
    private Integer port;
    @Field
    private String className;
    @Field
    private String group;
    @Field
    private String version;
    @Field
    private String method;
    @Field
    private List<DubboParam<String, String>> params;

    @Field
    private List<Pair<String, Object>> attachments;

}
