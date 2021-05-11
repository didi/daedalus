package com.didichuxing.daedalus.entity.pipeline;

import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Data
public class FlowEntity {

    @Field
    private List<BaseStepEntity> steps;

    @Field
    private List<EdgeEntity> edges;
}
