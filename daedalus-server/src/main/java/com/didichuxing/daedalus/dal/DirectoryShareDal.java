package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.directory.DirectoryShareEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Repository
public class DirectoryShareDal {

    @Autowired
    private MongoTemplate mongoTemplate;


    public List<DirectoryShareEntity> findByUserName(String sharer) {
        Query query = Query.query(Criteria.where("sharer").is(sharer)).with(Sort.by(Sort.Order.desc("createTime")));

        return mongoTemplate.find(query, DirectoryShareEntity.class);
    }


    public DirectoryShareEntity save(DirectoryShareEntity directoryShareEntity) {
        return mongoTemplate.save(directoryShareEntity);
    }

    public DirectoryShareEntity findByLinkId(String linkId) {
        return mongoTemplate.findOne(Query.query(Criteria.where("linkId").is(linkId)), DirectoryShareEntity.class);
    }

}
