package com.didichuxing.daedalus.dal.adaptor;

import com.didichuxing.daedalus.dal.PipelineDal;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author : jiangxinyu
 * @date : 2020/6/24
 */
@Repository
public class PipelineDalAdaptor {
    @Autowired
    private PipelineDal pipelineDal;

    public PipelineEntity queryById(String pipelineId) {
        if (EnvUtil.isOffline()) {
            return PipelineConverter.dtoToEntity(Context.getRequest().getPipelines().get(pipelineId));
        } else {
            return pipelineDal.queryById(pipelineId);
        }
    }
}
