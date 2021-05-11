package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;

/**
 * @author : jiangxinyu
 * @date : 2020/4/20
 */
public interface Digger {

    /**
     * 是否挖掘
     *
     * @param pipelineContext
     * @return
     */
    default boolean condition(PipelineContext pipelineContext) {
        return true;
    }

    /**
     * 数据挖掘
     *
     * @param pipelineContext
     */
    void dig(PipelineContext pipelineContext);

    default boolean isType(PipelineContext pipelineContext, ExecTypeEnum execTypeEnum) {
        return pipelineContext.get(ExecuteRequest.class).getExecType() == execTypeEnum;
    }
}
