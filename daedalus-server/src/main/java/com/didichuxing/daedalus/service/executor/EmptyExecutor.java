package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.EmptyStepEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class EmptyExecutor extends Executor<EmptyStepEntity> {


    @Override
    protected void exec(PipelineContext pipelineContext, EmptyStepEntity step) {
        appendLog(pipelineContext, step, "空白Step执行完毕！");
    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.EMPTY;
    }
}
