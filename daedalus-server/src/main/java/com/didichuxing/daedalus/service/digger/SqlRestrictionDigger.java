package com.didichuxing.daedalus.service.digger;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.pipeline.PipelineEntity;
import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import com.didichuxing.daedalus.entity.step.MysqlStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * @author : jiangxinyu
 * @date : 2020/7/16
 */
@Component
public class SqlRestrictionDigger implements Digger {

    @Override
    public boolean condition(PipelineContext pipelineContext) {
        PipelineEntity pipelineEntity = pipelineContext.get(PipelineEntity.class);
        return pipelineEntity.getFlow().getSteps()
                .stream()
                .anyMatch(step -> step.getStepType().equals(StepTypeEnum.MYSQL));
    }

    @Override
    public void dig(PipelineContext pipelineContext) {
        pipelineContext.get(PipelineEntity.class)
                .getFlow()
                .getSteps()
                .stream()
                .filter(step -> step.getStepType().equals(StepTypeEnum.MYSQL))
                .forEach(this::process);
    }


    private void process(BaseStepEntity step) {
        if (!step.getStepType().equals(StepTypeEnum.MYSQL)) {
            return;
        }
        MysqlStepEntity mysqlStep = (MysqlStepEntity) step;
        String sql = mysqlStep.getSql();
        if (StringUtils.isBlank(sql)) {
            throw new ExecuteException("SQL内容为空！");
        }
        String newSql = sql.replace("\r", "").replace("\n", "");
        if (newSql.endsWith(";")) {
            newSql = StringUtils.removeEnd(newSql, ";");
        }
        String[] sqls = newSql.split(";");

        //todo 如果是show 这种呢？
        long selectCount = Arrays.stream(sqls)
                .filter(rawSql -> StringUtils.startsWithIgnoreCase(rawSql, "select"))
                .count();
        if (selectCount > 1) {
            throw new ExecuteException("一个Step中的SELECT语句只能一个！Step:" + step.getName() + "中超过一个!");
        }

    }
}
