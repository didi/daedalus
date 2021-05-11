package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.common.enums.TaskStatusEnum;
import com.didichuxing.daedalus.entity.schedule.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Repository
public class TaskDal {

    @Autowired
    private MongoTemplate mongoTemplate;


    public TaskEntity findByScheduleIdAndDate(String scheduleId, Date date) {
        Query query = Query.query(Criteria.where("scheduleId").is(scheduleId).and("executeTime").is(date));
        return mongoTemplate.findOne(query, TaskEntity.class);
    }


    public List<TaskEntity> findRunnableTasks() {
        Query query = Query.query(Criteria.where("status").is(TaskStatusEnum.INITED).and("executeTime").lte(new Date()));
        return mongoTemplate.find(query, TaskEntity.class);
    }

    public TaskEntity save(TaskEntity taskEntity) {
        return mongoTemplate.save(taskEntity);
    }

    public long updateStatusByScheduleId(String scheduleId, TaskStatusEnum status) {
        Query query = Query.query(Criteria.where("scheduleId").is(scheduleId).and("status").in(Arrays.asList(TaskStatusEnum.INITED)));
        Update update = Update.update("status", status);
        return mongoTemplate.updateMulti(query, update, TaskEntity.class).getModifiedCount();
    }

    public long updateStatusById(String id, TaskStatusEnum status) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = Update.update("status", status);
        return mongoTemplate.updateMulti(query, update, TaskEntity.class).getModifiedCount();
    }
}
