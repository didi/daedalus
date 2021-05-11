package com.didichuxing.daedalus.aop;

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
 * @date : 2020/5/8
 */
@Aspect
@Slf4j
@Component
public class TimeLogAspect {

    @Pointcut("@annotation(com.didichuxing.daedalus.aop.TimeLog)")
    public void pointcut() {
    }

    @Around(value = "pointcut()")
    public Object executor(ProceedingJoinPoint joinPoint) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        String classname = joinPoint.getTarget().getClass().getSimpleName();
        String name = joinPoint.getSignature().getName();
        Object result = joinPoint.proceed();
        log.info("{}.{},耗时:{}ms", classname, name, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
