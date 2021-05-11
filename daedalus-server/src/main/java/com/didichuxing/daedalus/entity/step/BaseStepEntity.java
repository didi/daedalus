package com.didichuxing.daedalus.entity.step;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.additional.AssertEntity;
import com.didichuxing.daedalus.entity.step.additional.ConditionEntity;
import com.didichuxing.daedalus.entity.step.variables.ExtractVarEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Objects;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */

@Setter
@Getter
public abstract class BaseStepEntity {

    @Field
    private String id;

    @Field
    private String name;

    @Field
    private String remark;

    @Field
    private ConditionEntity condition;

    @Field
    private StepTypeEnum stepType;

    @Field
    private Integer delay;

    @Field
    private String alias;

    @Field
    private String preStepScript;

    @Field
    private String postStepScript;

    @Field
    private List<ExtractVarEntity> extractVars;

    @Field
    private List<AssertEntity> asserts;


    @Field
    private Boolean output;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseStepEntity that = (BaseStepEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }


}
