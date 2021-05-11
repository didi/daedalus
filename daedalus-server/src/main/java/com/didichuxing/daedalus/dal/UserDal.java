package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.UserEntity;
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
public class UserDal {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 查询用户相关
     *
     * @param username 拼音名
     * @return user
     */
    public UserEntity queryUser(String username) {
        return mongoTemplate.findOne(Query.query(Criteria.where("username").is(username)), UserEntity.class);
    }


    public UserEntity save(UserEntity userEntity) {
        return mongoTemplate.save(userEntity);
    }
}
