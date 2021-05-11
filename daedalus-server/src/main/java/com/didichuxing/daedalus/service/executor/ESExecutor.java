package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.ESStepEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component("esExecutor")
@Slf4j
public class ESExecutor extends Executor<ESStepEntity> {
    @Override
    protected void exec(PipelineContext pipelineContext, ESStepEntity step) {

    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.ES;
    }
}
