package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.common.enums.ScheduleStatusEnum;
import com.didichuxing.daedalus.entity.schedule.ScheduleEntity;
import com.google.common.collect.Lists;
import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Repository
public class ScheduleDal {

    @Autowired
    private MongoTemplate mongoTemplate;


    public long updateStatus(String scheduleId, ScheduleStatusEnum status) {
        Query query = Query.query(Criteria.where("_id").is(scheduleId));
        Update update = Update.update("status", status);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, ScheduleEntity.class);
        return updateResult.getModifiedCount();
    }


    public ScheduleEntity queryById(String scheduleId) {
        return mongoTemplate.findById(scheduleId, ScheduleEntity.class);
    }

    public ScheduleEntity save(ScheduleEntity scheduleEntity) {
        return mongoTemplate.save(scheduleEntity);
    }


    public Page<ScheduleEntity> list(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Query query = basicQuery().with(pageRequest).with(sortByTime());
        List<ScheduleEntity> instanceEntities = mongoTemplate.find(query, ScheduleEntity.class);
        return new PageImpl<>(instanceEntities, pageRequest, mongoTemplate.count(query, ScheduleEntity.class));
    }

    public List<ScheduleEntity> listAllRunnable() {
        return mongoTemplate.find(new Query(Criteria.where("status").in(Lists.newArrayList(ScheduleStatusEnum.ENABLED, ScheduleStatusEnum.RUNNING))), ScheduleEntity.class);
    }

    private Sort sortByTime() {
        return Sort.by(Sort.Order.desc("createTime"));
    }

    private Query basicQuery() {
        Criteria criteria = Criteria.where("status").in(Lists.newArrayList(ScheduleStatusEnum.ENABLED, ScheduleStatusEnum.DISABLED, ScheduleStatusEnum.RUNNING));
        return new Query(criteria);
    }


}
