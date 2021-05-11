package com.didichuxing.daedalus.dal;

import com.didichuxing.daedalus.entity.pipeline.PipelineSnapshotEntity;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;

/**
 * @author : jiangxinyu
 * @date : 2020/9/11
 */
@Repository
public class PipelineSnapshotDal {
    @Resource
    private MongoTemplate mongoTemplate;

    public PipelineSnapshotEntity insert(PipelineSnapshotEntity pipeline) {
        return mongoTemplate.insert(pipeline);
    }
}
