package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/17
 */
@Repository
public class EnvDal extends BizRepository<EnvGroupEntity, String> {


    @Autowired
    private MongoTemplate mongoTemplate;

    public EnvDal(MongoEntityInformation<EnvGroupEntity, String> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
    }


    public Page<EnvGroupEntity> findAll(String name, int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Query query = basicQuery();
        if (StringUtils.isNotBlank(name)) {
            query.addCriteria(like("name", name));
        }
        long count = mongoTemplate.count(query, EnvGroupEntity.class);

        query.with(pageRequest).with(sortByTime());
        List<EnvGroupEntity> envGroupEntities = mongoTemplate.find(query, EnvGroupEntity.class);
        return new PageImpl<>(envGroupEntities, pageRequest, count);

    }

    public EnvGroupEntity save(EnvGroupEntity entity) {
        if (entity.getId() == null) {
            return mongoTemplate.insert(entity);
        }

        Query query = Query.query(Criteria.where("_id").is(entity.getId()));
        Update update = Update.update("name", entity.getName())
                .set("data", entity.getData())
                .set("clusterInfo", entity.getClusterInfo())
                .set("bizLine", entity.getBizLine());
        return mongoTemplate.update(EnvGroupEntity.class)
                .matching(query).apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true))
                .findAndModifyValue();

    }


}
