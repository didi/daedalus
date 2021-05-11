package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.log.LogEntity;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/17
 */
@Repository
public class LogDal extends BizRepository<LogEntity, String> {


    @Autowired
    private MongoTemplate mongoTemplate;

    public LogDal(MongoEntityInformation<LogEntity, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }


    public Page<LogEntity> queryByPipelineId(String pipelineId, int page, int pageSize) {
        Query query = Query.query(Criteria.where("pipelineId").is(pipelineId));
        long count = mongoTemplate.count(query, LogEntity.class);
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        query.with(Sort.by(Sort.Order.desc("createTime"))).with(pageRequest);
        List<LogEntity> logEntities = mongoTemplate.find(query, LogEntity.class);
        return new PageImpl<>(logEntities, pageRequest, count);
    }

    public Page<String> queryByCount(int page, int pageSize) {
        GroupOperation groupOperation = Aggregation.group("pipelineId").count().as("times");
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("times")));
        SkipOperation skipOperation = Aggregation.skip(Long.valueOf(page * pageSize));
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        Aggregation aggregation = Aggregation.newAggregation(groupOperation, sortOperation, skipOperation, limitOperation);

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "log", Document.class);
        AggregationResults<Document> counts = mongoTemplate.aggregate(Aggregation.newAggregation(groupOperation), "log", Document.class);
        List<String> pipelineIds = results.getMappedResults()
                .stream()
                .map(doc -> doc.getString("_id"))
                .collect(Collectors.toList());
        return new PageImpl<>(pipelineIds, PageRequest.of(page, pageSize), counts.getMappedResults().size());
    }

    @Override
    public Optional<LogEntity> findById(String id) {
        Query query = queryById(id);
        return Optional.ofNullable(
                mongoTemplate.findOne(query, LogEntity.class));
    }
}
