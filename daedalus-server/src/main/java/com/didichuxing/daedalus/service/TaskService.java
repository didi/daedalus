package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.dto.User;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.common.enums.TaskStatusEnum;
import com.didichuxing.daedalus.dal.TaskDal;
import com.didichuxing.daedalus.entity.schedule.TaskEntity;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author : jiangxinyu
 * @date : 2020/10/26
 */
@Slf4j
@Service
public class TaskService {
    private static final ExecutorService TASK_EXECUTOR = new ThreadPoolExecutor(60,
            60,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("schedule-task-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );
    @Autowired
    private TaskDal taskDal;
    @Autowired
    private DispatchService dispatchService;
    @Autowired
    private PipelineService pipelineService;

    //todo 注意：只支持单点部署，需要多机请加锁
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        if (EnvUtil.isOffline()) {
            return;
        }
        List<TaskEntity> runnableTasks = taskDal.findRunnableTasks();
        if (CollectionUtils.isEmpty(runnableTasks)) {
            return;
        }

        for (TaskEntity task : runnableTasks) {
            TASK_EXECUTOR.execute(() -> {
                execute(task);
            });
        }

    }

    private void execute(TaskEntity task) {
        try {
            User user = new User();
            user.setUsernameCN("定时任务");
            user.setUsername("Schedule");
            user.setCookie("");
            Context.setUser(user);

            ExecuteRequest executeRequest = new ExecuteRequest();
            executeRequest.setInputs(task.getInputs());
            executeRequest.setPipelineId(task.getPipelineId());
            executeRequest.setEnv(task.getEnv());
            executeRequest.setExecType(ExecTypeEnum.NORMAL);
            if (EnvUtil.isTest()) {
                pipelineService.execute(executeRequest);
            } else {
                dispatchService.doDispatch(executeRequest);
            }
        } catch (Exception e) {
            log.error("定时任务执行异常！", e);
        } finally {
            taskDal.updateStatusById(task.getId(), TaskStatusEnum.END);
            Context.clear();
        }
    }
}
