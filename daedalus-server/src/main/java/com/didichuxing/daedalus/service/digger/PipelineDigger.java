package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.dal.adaptor.PipelineDalAdaptor;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.handler.PipelineConverter;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.util.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PipelineDigger implements Digger {

    @Autowired
    private PipelineDalAdaptor pipelineDal;

    @Override
    public void dig(PipelineContext pipelineContext) {
        ExecuteRequest executeRequest = pipelineContext.get(ExecuteRequest.class);
        if (executeRequest.getExecType() == ExecTypeEnum.DEBUG) {
            pipelineContext.put(PipelineEntity.class, PipelineConverter.dtoToEntity(executeRequest.getPipeline()));
        } else {
            String pipelineId = pipelineContext.getPipelineId();
            PipelineEntity pipelineEntity = pipelineDal.queryById(pipelineId);
            Validator.notNull(pipelineEntity, ErrorCode.PIPELINE_NOT_FOUND);
            pipelineContext.put(PipelineEntity.class, pipelineEntity);
        }


    }


}
