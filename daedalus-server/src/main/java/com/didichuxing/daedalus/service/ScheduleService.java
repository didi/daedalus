package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.dto.schedule.Schedule;
import com.didichuxing.daedalus.common.enums.ScheduleStatusEnum;
import com.didichuxing.daedalus.common.enums.TaskStatusEnum;
import com.didichuxing.daedalus.dal.ScheduleDal;
import com.didichuxing.daedalus.dal.TaskDal;
import com.didichuxing.daedalus.entity.schedule.ScheduleEntity;
import com.didichuxing.daedalus.entity.schedule.TaskEntity;
import com.didichuxing.daedalus.handler.SimpleConverter;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/10/23
 */
@Service
@Slf4j
public class ScheduleService {

    private static final ExecutorService SCHEDULE_EXECUTOR = new ThreadPoolExecutor(10,
            20,
            60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new ThreadFactoryBuilder().setNameFormat("schedule-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    @Autowired
    private ScheduleDal scheduleDal;
    @Autowired
    private TaskDal taskDal;


    public String save(Schedule schedule) {
        if (!CronSequenceGenerator.isValidExpression(schedule.getCronRule())) {
            throw new BizException("cron表达式有误！");
        }
        ScheduleEntity scheduleEntity = SimpleConverter.convert(schedule, ScheduleEntity.class);
        if (StringUtils.isNotBlank(schedule.getId())) {
            ScheduleEntity oldEntity = scheduleDal.queryById(schedule.getId());
            if (oldEntity == null) {
                throw new BizException("定时任务不存在！");
            }
            scheduleEntity.setStatus(oldEntity.getStatus());
            scheduleEntity.setCreateTime(oldEntity.getCreateTime());
            //禁用所有task
            taskDal.updateStatusByScheduleId(schedule.getId(), TaskStatusEnum.INVALID);
        }

        scheduleEntity.setCreator(Context.getUser().getUsername());
        scheduleEntity.setCreatorCN(Context.getUser().getUsernameCN());
        return scheduleDal.save(scheduleEntity).getId();
    }

    public void enable(String scheduleId) {
        scheduleDal.updateStatus(scheduleId, ScheduleStatusEnum.ENABLED);
    }

    public void disable(String scheduleId) {
        //禁用所有task
        taskDal.updateStatusByScheduleId(scheduleId, TaskStatusEnum.INVALID);
        scheduleDal.updateStatus(scheduleId, ScheduleStatusEnum.DISABLED);
    }

    public void delete(String scheduleId) {
        //禁用所有task
        taskDal.updateStatusByScheduleId(scheduleId, TaskStatusEnum.INVALID);
        scheduleDal.updateStatus(scheduleId, ScheduleStatusEnum.DELETED);
    }

    public void running(String scheduleId) {
        scheduleDal.updateStatus(scheduleId, ScheduleStatusEnum.RUNNING);
    }

    public PageResponse<List<Schedule>> list(int page, int pageSize) {
        Page<ScheduleEntity> list = scheduleDal.list(page, pageSize);
        List<Schedule> schedules = list.stream().map(scheduleEntity -> SimpleConverter.convert(scheduleEntity, Schedule.class)).collect(Collectors.toList());
        return PageResponse.of(page, pageSize, list.getTotalPages(), list.getTotalElements(), schedules);
    }


    //todo 注意：只支持单点部署，需要多机请加锁
    @Scheduled(cron = "0/5 * * * * ?")
    public void scan() {
        if (EnvUtil.isOffline()) {
            return;
        }
        List<ScheduleEntity> scheduleEntities = scheduleDal.listAllRunnable();
        if (CollectionUtils.isNotEmpty(scheduleEntities)) {
            for (ScheduleEntity scheduleEntity : scheduleEntities) {
                SCHEDULE_EXECUTOR.execute(() -> {
                    String cronRule = scheduleEntity.getCronRule();
                    if (StringUtils.isBlank(cronRule)) {
                        log.error("{} cronRule为空！", scheduleEntity.getId());
                        return;
                    }

                    CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator(cronRule);
                    Date nextDate = cronSequenceGenerator.next(new Date());


                    TaskEntity existTask = taskDal.findByScheduleIdAndDate(scheduleEntity.getId(), nextDate);
                    if (existTask != null) {
                        log.info("{} @ {} task已存在！", scheduleEntity.getId(), nextDate);
                        return;
                    }

                    TaskEntity taskEntity = new TaskEntity();
                    taskEntity.setScheduleId(scheduleEntity.getId());
                    taskEntity.setInputs(scheduleEntity.getInputs());
                    taskEntity.setStatus(TaskStatusEnum.INITED);
                    taskEntity.setPipelineId(scheduleEntity.getPipelineId());
                    taskEntity.setExecuteTime(nextDate);
                    taskEntity.setEnv(scheduleEntity.getEnv());
                    taskDal.save(taskEntity);
                });
            }
        }
    }



}
