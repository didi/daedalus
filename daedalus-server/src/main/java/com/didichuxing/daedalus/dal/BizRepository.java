package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.common.enums.StatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * @author : jiangxinyu
 * @date : 2020/4/17
 */
@NoRepositoryBean
public abstract class BizRepository<T, ID> {

    private final MongoOperations mongoOperations;
    private final MongoEntityInformation<T, ID> entityInformation;


    public BizRepository(MongoEntityInformation<T, ID> metadata, MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
        this.entityInformation = metadata;
    }


    public T insert(T entity) {
        return mongoOperations.insert(entity);
    }


    protected Query queryById(ID id) {
        return Query.query(Criteria.where("_id").is(id));
    }

    protected Update deleteStatus() {
        return Update.update("status", StatusEnum.DELETED);
    }

    public Page<T> findAll(Pageable pageable) {

        Assert.notNull(pageable, "Pageable must not be null!");

        long count = count();
        List<T> list = findAll(new Query().with(pageable));

        return new PageImpl<>(list, pageable, count);
    }

    public Optional<T> findById(ID id) {

        Assert.notNull(id, "The given id must not be null!");

        Query query = queryById(id).addCriteria(okStatus());
        return Optional.ofNullable(
                mongoOperations.findOne(query, entityInformation.getJavaType(), entityInformation.getCollectionName()));
    }

    public List<T> findByIds(List<ID> ids) {
        if (ids == null || ids.size() == 0) {
            return new ArrayList<>();
        }
        Query query = Query.query(Criteria.where("_id").in(ids));
        return mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    public List<T> findAll(@Nullable Query query) {

        if (query == null) {
            return Collections.emptyList();
        }

        return mongoOperations.find(query, entityInformation.getJavaType(), entityInformation.getCollectionName());
    }

    public long count() {
        return mongoOperations.count(new Query(), entityInformation.getCollectionName());
    }


    /**
     * 修改status
     *
     * @param id
     */
    public void delById(ID id) {
        mongoOperations.updateFirst(queryById(id), deleteStatus(), entityInformation.getJavaType());
    }

    protected Criteria like(String filed, String value) {
        return Criteria.where(filed).regex(".*" + value + ".*");
    }

    protected Sort sortByTime() {
        return Sort.by(Sort.Order.desc("createTime"));
    }


    protected Query basicQuery() {
        Criteria criteria = okStatus();
        return new Query(criteria);
    }

    private Criteria okStatus() {
        return Criteria.where("status").is(StatusEnum.OK);
    }


}
