package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.common.enums.StatusEnum;
import com.didichuxing.daedalus.entity.log.LogEntity;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.mongodb.client.result.UpdateResult;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.bson.Document;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndReplaceOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : jiangxinyu
 * @date : 2020/3/23
 */
@Repository
public class PipelineDal {

    @Resource
    private MongoTemplate mongoTemplate;

    public PipelineEntity queryById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id).and("status").is(StatusEnum.OK));
        return mongoTemplate.findOne(query, PipelineEntity.class);
    }

    public Page<PipelineEntity> queryBy(String owner, List<Integer> bizLine, List<String> tags, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Query query = basicQuery(bizLine, tags).addCriteria(Criteria.where("creator").is(owner));
        long count = mongoTemplate.count(query, PipelineEntity.class);
        query.with(pageRequest).with(sortByTime());
        List<PipelineEntity> pipelineEntities = mongoTemplate.find(query, PipelineEntity.class);
        return new PageImpl<>(pipelineEntities, pageRequest, count);
    }


    public PipelineEntity insert(PipelineEntity pipeline) {
        return mongoTemplate.insert(pipeline);
    }

    public boolean update(PipelineEntity pipeline) {
        Query query = Query.query(Criteria.where("_id").is(pipeline.getId()));
        Optional<PipelineEntity> old = mongoTemplate.update(PipelineEntity.class)
                .matching(query)
                .replaceWith(pipeline)
                .withOptions(FindAndReplaceOptions.options().upsert())
                .findAndReplace();
        return old.isPresent();
    }

    public boolean delete(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = Update.update("status", StatusEnum.DELETED);
        UpdateResult updateResult = mongoTemplate.updateFirst(query, update, PipelineEntity.class);
        return updateResult.getModifiedCount() == 1;
    }

    public Page<PipelineEntity> queryByIds(List<String> pipelineIds, List<Integer> bizLine, List<String> tags, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Query query = basicQuery(bizLine, tags).addCriteria(Criteria.where("_id").in(pipelineIds));

        long count = mongoTemplate.count(query, PipelineEntity.class);
        query.with(sortByTime());

        List<PipelineEntity> pipelineEntities = mongoTemplate.find(query, PipelineEntity.class);
        return new PageImpl<>(pipelineEntities, pageRequest, count);
    }

    public List<PipelineEntity> queryByIds(List<String> pipelineIds) {
        Query query = Query.query(Criteria.where("_id").in(pipelineIds));
        return mongoTemplate.find(query, PipelineEntity.class);
    }

    public Page<PipelineEntity> queryAll(List<Integer> bizLine, List<String> tags, int page, int size) {
        Query query = basicQuery(bizLine, tags);
        long count = mongoTemplate.count(query, PipelineEntity.class);

        PageRequest pageRequest = PageRequest.of(page, size);

        List<PipelineEntity> pipelineEntities = mongoTemplate.find(query.with(sortByTime()).with(pageRequest), PipelineEntity.class);

        return PageableExecutionUtils.getPage(pipelineEntities, pageRequest, () -> count);
    }


    public Page<PipelineEntity> searchByTag(List<String> tags, int page, int size) {
        Query query = basicQuery(null, tags);
        long count = mongoTemplate.count(query, PipelineEntity.class);

        PageRequest pageRequest = PageRequest.of(page, size);

        List<PipelineEntity> pipelineEntities = mongoTemplate.find(query.with(sortByTime()).with(pageRequest), PipelineEntity.class);

        return PageableExecutionUtils.getPage(pipelineEntities, pageRequest, () -> count);
    }

    public List<PipelineEntity> search(String key) {
        Pattern pattern = Pattern.compile(".*" + key + ".*", Pattern.CASE_INSENSITIVE);
        Criteria criteria = new Criteria();
        criteria.orOperator(Criteria.where("creator").regex(pattern),
                Criteria.where("name").regex(pattern),
                Criteria.where("creatorCN").regex(pattern),
                Criteria.where("tags").regex(pattern)
        );
        Query query = Query.query(criteria).with(sortByTime());

        return mongoTemplate.find(query, PipelineEntity.class);
    }


    @Deprecated
    public Page<PipelineRecord> queryRecentUsed2(String username, List<Integer> bizLine, List<String> tags, int page, int size) {
        ProjectionOperation projectionOperation = Aggregation.project("pid").and(ConvertOperators.ToObjectId.toObjectId("$pipelineId")).as("pid").and(Aggregation.ROOT).as("log");
        LookupOperation lookupOperation = LookupOperation.newLookup().from("pipeline").localField("pid").foreignField("_id").as("pipeline");
        UnwindOperation unwindOperation = Aggregation.unwind("pipeline");

        Criteria criteria = Criteria.where("pipeline.status").is(StatusEnum.OK);
        if (CollectionUtils.isNotEmpty(bizLine)) {
            criteria.and("pipeline.bizLine").in(bizLine);
        }
        if (CollectionUtils.isNotEmpty(tags)) {
            criteria.and("pipeline.tags").in(tags);
        }
        MatchOperation pipelineMatch = Aggregation.match(criteria);

        Criteria criteria1 = Criteria.where("username").is(username);
        MatchOperation recordMatch = Aggregation.match(criteria1);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("log.createTime")));
        SkipOperation skipOperation = Aggregation.skip(Long.valueOf(page * size));
        LimitOperation limitOperation = Aggregation.limit(size);

        Aggregation finalAggregation = Aggregation.newAggregation(recordMatch, projectionOperation, lookupOperation, unwindOperation, pipelineMatch, sortOperation, skipOperation, limitOperation);

        Aggregation countAggregation = Aggregation.newAggregation(recordMatch, projectionOperation, lookupOperation, unwindOperation, pipelineMatch);

        AggregationResults<PipelineRecord> results = mongoTemplate.aggregate(finalAggregation, "log", PipelineRecord.class);
        AggregationResults<Document> counts = mongoTemplate.aggregate(countAggregation, "log", Document.class);
        queryRecentUsed2(username, bizLine, tags, page, size);
        return new PageImpl<>(results.getMappedResults(), PageRequest.of(page, size), counts.getMappedResults().size());
    }

    public Page<PipelineRecord> queryRecentUsed(String username, List<Integer> bizLine, List<String> tags, int page, int size) {
        ProjectionOperation projectionOperation = Aggregation.project("pid").and(ConvertOperators.ToString.toString("$_id")).as("pid").and(Aggregation.ROOT).as("pipeline");
        LookupOperation lookupOperation = LookupOperation.newLookup().from("log").localField("pid").foreignField("pipelineId").as("log");
        UnwindOperation unwindOperation = Aggregation.unwind("log");

        Criteria criteria = Criteria.where("status").is(StatusEnum.OK);
        if (CollectionUtils.isNotEmpty(bizLine)) {
            criteria.and("bizLine").in(bizLine);
        }
        if (CollectionUtils.isNotEmpty(tags)) {
            criteria.and("tags").in(tags);
        }
        MatchOperation pipelineMatch = Aggregation.match(criteria);

        Criteria criteria1 = Criteria.where("log.username").is(username);
        MatchOperation recordMatch = Aggregation.match(criteria1);

        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Order.desc("log.createTime")));
        SkipOperation skipOperation = Aggregation.skip(Long.valueOf(0 * size));
        LimitOperation limitOperation = Aggregation.limit(5);

        GroupOperation groupOperation = Aggregation.group("pid").first("pipeline").as("pipeline").first("log").as("log");


        Aggregation finalAggregation = Aggregation.newAggregation(pipelineMatch, projectionOperation, lookupOperation, unwindOperation, recordMatch, groupOperation, sortOperation, skipOperation, limitOperation);


        AggregationResults<PipelineRecord> results = mongoTemplate.aggregate(finalAggregation, "pipeline", PipelineRecord.class);
        return new PageImpl<>(results.getMappedResults(), PageRequest.of(0, 5), results.getMappedResults().size());
    }

    public Map<String, Long> queryTags() {
        Query query = new Query().with(sortByTime()).limit(50);
        Map<String, Long> result = mongoTemplate.find(query, PipelineEntity.class)
                .stream()
                .flatMap((Function<PipelineEntity, Stream<String>>) pipelineEntity -> pipelineEntity.getTags() == null ? Stream.empty() : pipelineEntity.getTags().stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return result;

    }

    private Sort sortByTime() {
        return Sort.by(Sort.Order.desc("createTime"));
    }


    private Query basicQuery(List<Integer> bizLine, List<String> tags) {
        Criteria criteria = basicCriteria(bizLine, tags);
        return new Query(criteria);
    }

    private Criteria basicCriteria(List<Integer> bizLine, List<String> tags) {
        Criteria criteria = Criteria.where("status").is(StatusEnum.OK);
        if (CollectionUtils.isNotEmpty(bizLine)) {
            criteria.and("bizLine").in(bizLine);
        }
        if (CollectionUtils.isNotEmpty(tags)) {
            criteria.and("tags").in(tags);
        }
        return criteria;
    }


    @Setter
    @Getter
    public static class PipelineRecord {

        private LogEntity log;
        private PipelineEntity pipeline;
    }
}
