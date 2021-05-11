package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.dal.adaptor.PipelineDalAdaptor;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.ImportStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.service.PipelineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class ImportExecutor extends Executor<ImportStepEntity> {

    @Autowired
    private PipelineDalAdaptor pipelineDal;
    @Autowired
    private PipelineService pipelineService;

    @Override
    protected void exec(PipelineContext pipelineContext, ImportStepEntity step) {

        PipelineEntity pipelineEntity = pipelineDal.queryById(step.getPipelineId());
        if (pipelineEntity == null) {
            throw new ExecuteException("Step:"+step.getName()+" 引入的流水线不存在！pipelineId:" + step.getPipelineId());
        }

        //引入的流水线只在当前环境运行
        ExecuteRequest request = new ExecuteRequest();
        request.setPipelineId(step.getPipelineId());
        request.setEnv(step.getEnv());
        request.setInputs(step.getInputs());
        request.setExecType(ExecTypeEnum.NORMAL);
        ExecuteResult executeResult = pipelineService.execute(request);

        //变量是隔离的，只要输出就可以了，防止混乱&变量重复、提高出错率等问题
        //结果
        if (executeResult.isSuccess()) {
            createResponse(pipelineContext, step, executeResult.getResult());
        } else {
            throw new ExecuteException(pipelineEntity.getName() + "运行失败！" + executeResult.getExceptionMsg());
        }

    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.IMPORT;
    }
}
