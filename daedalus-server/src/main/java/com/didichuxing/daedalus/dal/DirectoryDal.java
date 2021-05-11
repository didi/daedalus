package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.directory.DirectoryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@Repository
public class DirectoryDal {

    @Autowired
    private MongoTemplate mongoTemplate;


    public DirectoryEntity findByUserName(String username) {
        Query query = Query.query(Criteria.where("username").is(username));
        return mongoTemplate.findOne(query, DirectoryEntity.class);
    }


    public DirectoryEntity save(DirectoryEntity directory) {
        return mongoTemplate.save(directory);
    }


}
