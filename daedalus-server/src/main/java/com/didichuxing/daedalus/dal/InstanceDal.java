package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.common.enums.StatusEnum;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@Repository
public class InstanceDal {

    @Autowired
    private MongoTemplate mongoTemplate;


    public Page<InstanceEntity> list(String instanceType, int page, int pageSize, String name, String ip) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        Query query = basicQuery().addCriteria(typeCriteria(instanceType)).with(pageRequest).with(sortByTime());
        if (StringUtils.isNotBlank(name)) {
            query.addCriteria(like("name", name));
        }
        if (StringUtils.isNotBlank(ip)) {
            if ("http".equalsIgnoreCase(instanceType)) {
                query.addCriteria(like("url", ip));
            } else {
                query.addCriteria(like("ip", ip));
            }
        }
        List<InstanceEntity> instanceEntities = mongoTemplate.find(query, InstanceEntity.class);
        return new PageImpl<>(instanceEntities, pageRequest, countByType(instanceType));
    }


    public long countByType(String insType) {
        return mongoTemplate.count(basicQuery().addCriteria(typeCriteria(insType)), InstanceEntity.class);
    }

    public void delete(String instanceId) {
        Update update = Update.update("status", StatusEnum.DELETED);
        mongoTemplate.updateFirst(Query.query(Criteria.where("_id").is(instanceId)), update, InstanceEntity.class);
    }

    public InstanceEntity save(InstanceEntity entity) {
        if (entity.getId() != null) {
            return update(entity);
        }
        return mongoTemplate.insert(entity);
    }

    public InstanceEntity findById(String instanceId) {
        return mongoTemplate.findById(instanceId, InstanceEntity.class);
    }

    public List<InstanceEntity> findByIds(Collection<String> instanceIds) {
        Query query = Query.query(Criteria.where("_id").in(instanceIds));
        return mongoTemplate.find(query, InstanceEntity.class);
    }

    public InstanceEntity update(InstanceEntity entity) {
        Query query = Query.query(Criteria.where("_id").is(entity.getId()));
        Update update = Update.update("name", entity.getName())
                .set("ip", entity.getIp())
                .set("port", entity.getPort())
                .set("username", entity.getUsername())
                .set("database", entity.getDatabase())
                .set("protocol", entity.getProtocol())
                .set("method", entity.getMethod())
                .set("url", entity.getUrl())
                .set("timeout", entity.getTimeout())
                .set("body", entity.getBody())
                .set("bodyType", entity.getBodyType())
                .set("formData", entity.getFormData())
                .set("headers", entity.getHeaders())
                .set("cookies", entity.getCookies())
                .set("urlParams", entity.getUrlParams())
                .set("cookieText", entity.getCookieText());

        if (entity.getPassword() != null && StringUtils.isNotBlank(entity.getPassword().replace("*", ""))) {
            update.set("password", entity.getPassword());
        }
        return mongoTemplate.update(InstanceEntity.class)
                .matching(query).apply(update)
                .withOptions(FindAndModifyOptions.options().upsert(true))
                .findAndModifyValue();
    }

    public InstanceEntity findByIdAndType(String instanceId, InstanceTypeEnum instanceType) {
        final Query query = basicQuery().addCriteria(Criteria.where("_id").is(instanceId)).addCriteria(typeCriteria(instanceType.name()));
        return mongoTemplate.findOne(query, InstanceEntity.class);
    }

    private Sort sortByTime() {
        return Sort.by(Sort.Order.desc("createTime"));
    }


    private Query basicQuery() {
        Criteria criteria = Criteria.where("status").is(StatusEnum.OK);
        return new Query(criteria);
    }

    private Criteria typeCriteria(String instanceType) {
        return Criteria.where("instanceType").is(instanceType);

    }


    private Criteria like(String filed, String value) {
        return Criteria.where(filed).regex(".*" + value + ".*");
    }
}
