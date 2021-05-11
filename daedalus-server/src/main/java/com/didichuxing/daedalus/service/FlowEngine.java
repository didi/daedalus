package com.didichuxing.daedalus.service;

import com.alibaba.ttl.threadpool.TtlExecutors;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.common.enums.ExecuteStatusEnum;
import com.didichuxing.daedalus.dal.adaptor.LogDalAdaptor;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.service.executor.Executor;
import com.didichuxing.daedalus.service.executor.ExecutorContainer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.didichuxing.daedalus.common.enums.ExecuteStatusEnum.*;
import static com.didichuxing.daedalus.common.enums.StepStatusEnum.*;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Service
@Slf4j
public class FlowEngine {

    private static final ThreadPoolExecutor CORE_SERVICE = new ThreadPoolExecutor(20, 40, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r -> new Thread(r, "step-executor-"));
    private static final ExecutorService EXECUTOR_SERVICE = TtlExecutors.getTtlExecutorService(CORE_SERVICE);


    @Autowired
    private ExecutorContainer executorContainer;
    @Autowired
    private LogDalAdaptor logDal;


    public void fire(PipelineContext pipelineContext) {

        Map<BaseStepEntity, Phaser> stepCount = new ConcurrentHashMap<>(16);
        Map<BaseStepEntity, ExecuteStatusEnum> executedRecord = new ConcurrentHashMap<>(32);
        List<List<BaseStepEntity>> paths = pipelineContext.getPaths();
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);
        AtomicReference<ExecuteStatusEnum> pipelineStatus = new AtomicReference<>(SUCCESS);


        if (CollectionUtils.isEmpty(paths)) {
            return;
        }


        paths.forEach(path -> path.forEach(step -> {
            Phaser phaser = stepCount.getOrDefault(step, new Phaser(0));
            phaser.register();
            stepCount.put(step, phaser);
        }));

        //设置已运行的step的状态
        if (executeRequest.getExecType()== ExecTypeEnum.RESUME) {
            LogEntity logEntity = pipelineContext.get(LogEntity.class);
            PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
            logEntity.getStepLogs().stream()
                    .filter(stepLogEntity -> stepLogEntity.getStepStatus() == RUN_SUCCESS)
                    .forEach(stepLogEntity -> {
                        String stepId = stepLogEntity.getStepId();
                        pipelineEntity.getFlow().getSteps()
                                .stream()
                                .filter(step -> step.getId().equals(stepId))
                                .findFirst()
                                .ifPresent(baseStepEntity -> executedRecord.put(baseStepEntity, SUCCESS));
                    });
        }


        //pipeline 运行是否成功，任意step失败就失败
        List<PathTask> tasks = paths.stream()
                .map(path -> new PathTask(stepCount, executedRecord, path, pipelineContext, pipelineStatus))
                .collect(Collectors.toList());
        try {
            List<Future<Boolean>> futures = EXECUTOR_SERVICE.invokeAll(tasks, 60 * 5, TimeUnit.SECONDS);
            futures.forEach(Future::isDone);
        } catch (InterruptedException e) {
            log.error("流水线执行超时！pipelineId:{}", pipelineContext.getPipelineId());
            throw new ExecuteException("流水线运行超时！");
        } finally {
            LogEntity logEntity = pipelineContext.get(LogEntity.class);
            logEntity.setRuntimeVars(pipelineContext.getVars());
            logEntity.setExecuteStatus(pipelineStatus.get());
            LogEntity entity = logDal.insert(logEntity);
            logEntity.setId(entity.getId());

        }

    }


    /**
     * 执行每条path
     */
    @RequiredArgsConstructor
    class PathTask implements Callable<Boolean> {

        @NonNull
        Map<BaseStepEntity, Phaser> stepCount;
        @NonNull
        Map<BaseStepEntity, ExecuteStatusEnum> executedRecord;//记录step运行状态
        @NonNull
        List<BaseStepEntity> path;
        @NonNull
        PipelineContext pipelineContext;
        @NonNull
        AtomicReference<ExecuteStatusEnum> pipelineStatus;
        ExecuteStatusEnum pathStatus;

        @Override
        public Boolean call() {
            for (BaseStepEntity step : path) {
                if (pathStatus == FAILED || pipelineStatus.get() == FAILED) {
                    break;
                }
                Phaser phaser = stepCount.get(step);
                if (pathStatus == MISMATCH) {
                    //因为条件不匹配执行失败 需要处理common step
                    phaser.arriveAndDeregister();
                    continue;
                }
                //step只执行一次
                if (executedRecord.containsKey(step)) {
                    continue;
                }

                Executor<BaseStepEntity> executor = executorContainer.getExecutor(step.getStepType());
                if (executor == null) {
                    log.error("不支持{}类型Step！", step.getStepType());
                    break;
                }
                phaser.arriveAndAwaitAdvance();


                //检查流水线状态
                if (pathStatus == FAILED || pipelineStatus.get() == FAILED) {
                    break;
                }


                //同时只能一个step执行
                synchronized (step) {
                    //再次检查是否被执行
                    if (executedRecord.get(step) != null) {
                        //path状态同步公共的状态，公共有失败的path不再执行
                        pathStatus = executedRecord.get(step);
                        log.info("Step:{} 已经被执行，跳过", step.getName());
                        continue;
                    }
                    log.info("执行Step:{}", step.getName());
                    ExecuteStatusEnum status = executor.execute(pipelineContext, step);
                    //记录运行结果
                    recordStatus(step, status);
                }
            }


            return true;
        }

        private void recordStatus(BaseStepEntity step, ExecuteStatusEnum status) {
            executedRecord.put(step, status);

            pipelineContext.get(LogEntity.class).getStepLogs().stream().filter(stepLog -> step.getId().equals(stepLog.getStepId())).findAny().ifPresent(stepLog -> {
                if (status == SUCCESS) {
                    stepLog.setStepStatus(RUN_SUCCESS);
                } else if (status == FAILED || status == ASSERT_FAILED) {
                    stepLog.setStepStatus(RUN_FAILED);
                } else if (status == MISMATCH) {
                    stepLog.setStepStatus(SKIPED);
                }
            });
            if (status == FAILED || status == ASSERT_FAILED) {
                pipelineStatus.set(status);
                pathStatus = status;
            } else if (status == MISMATCH) {
                pathStatus = status;
            }
        }
    }


}
