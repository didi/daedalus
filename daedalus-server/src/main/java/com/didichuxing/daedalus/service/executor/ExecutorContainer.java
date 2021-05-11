package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/4/24
 */
@Service
public class ExecutorContainer {

    private final Map<StepTypeEnum, Executor<BaseStepEntity>> executorMap = new HashMap<>();

    @Autowired
    private List<Executor> executors;

    @PostConstruct
    public void init() {
        executors.forEach(executor -> executorMap.put(executor.getStepType(), executor));

    }


    public Executor<BaseStepEntity> getExecutor(StepTypeEnum stepType) {
        return executorMap.get(stepType);
    }


}
