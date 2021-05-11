package com.didichuxing.daedalus.aop;

import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.executor.Executor;
import com.google.common.base.Stopwatch;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@Aspect
@Slf4j
@Component
public class ExecutorAspect {

    @Pointcut("execution(* com.didichuxing.daedalus.service.executor.*.execute(..))")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object executor(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        try {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Object[] args = joinPoint.getArgs();
            Executor executor = (Executor) joinPoint.getTarget();
            PipelineContext context = (PipelineContext) args[0];
            BaseStepEntity step = (BaseStepEntity) args[1];

            log.info("开始执行Step:{},StepId:{},type:{}", step.getName(), step.getId(), step.getStepType());
            executor.appendLog(context, step, "开始执行Step...");

            result = joinPoint.proceed();
            long elapsed = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            executor.appendLog(context, step, "Step执行完成,耗时:" + elapsed + "ms");
            log.info("Step:{}执行完成,pipelineId:{},type:{},耗时:{}ms", step.getName(), context.getPipelineId(), step.getStepType(), elapsed);
        } catch (Throwable throwable) {
            log.error("error", throwable);
            throw throwable;
        }

        return result;
    }


}
