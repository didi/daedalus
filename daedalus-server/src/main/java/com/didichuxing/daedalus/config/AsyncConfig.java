package com.didichuxing.daedalus.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : jiangxinyu
 * @date : 2020/5/12
 */
@Configuration
@Slf4j
public class AsyncConfig implements AsyncConfigurer, AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        log.error("async task error,method:{}", method.getName(), throwable);
    }

    @Override
    public Executor getAsyncExecutor() {
        return new ThreadPoolExecutor(20, 20, 10, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), r -> {
            Thread thread = new Thread(r);
            thread.setName("AsyncThread");
            return thread;
        });
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return this;
    }
}
