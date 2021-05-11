package com.didichuxing.daedalus.service.executor;

import com.didichuxing.daedalus.common.enums.StepTypeEnum;
import com.didichuxing.daedalus.entity.step.MysqlStepEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.InstanceCenter;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * @author : jiangxinyu
 * @date : 2020/4/13
 */
@Component
@Slf4j
public class MysqlExecutor extends Executor<MysqlStepEntity> {

    private static final String[] SELECT = new String[]{"select", "show"};
    private static final String[] UPDATE = new String[]{"update", "delete"};
    private static final String INSERT = "insert";
    @Autowired
    private InstanceCenter instanceCenter;

    @Override
    protected void exec(PipelineContext pipelineContext, MysqlStepEntity step) {
        String instanceId = step.getInstanceId();
        String sql = step.getSql();


        appendLog(pipelineContext, step, "执行SQL:" + sql);
        log.info("执行sql:{}", sql);
        //todo sql中不会有分号吧？

        try (Connection connection = instanceCenter.getMysql(instanceId);
             Statement statement = connection.createStatement()) {
            String newSql = sql.trim();
            List<String> sqls = Splitter.on(";").trimResults().omitEmptyStrings().splitToList(newSql);

            Object result = executeSqls(pipelineContext, step, statement, sqls);

            createResponse(pipelineContext, step, result);


        } catch (Exception e) {
            log.error("执行MYSQL Step出错！ sql:{}", sql, e);

            appendLog(pipelineContext, step, "SQL执行失败！Error Msg:" + e.getMessage());
            createResponse(pipelineContext, step, step.getName() + "执行失败!原因：" + e.getMessage());

            if (e instanceof ExecuteException) {
                throw (ExecuteException) e;
            } else {
                throw new ExecuteException("SQL:" + sql + "执行失败!" + e.getMessage());
            }
        }
    }

    private Object executeSqls(PipelineContext pipelineContext, MysqlStepEntity step, Statement statement, List<String> sqls) throws SQLException {
        //最后一条sql结果作为最后结果
        Object finalResult = null;
        for (String singleSql : sqls) {
            if (StringUtils.startsWithAny(singleSql.toLowerCase(), UPDATE)) {
                int effectRows = statement.executeUpdate(singleSql);
                appendLog(pipelineContext, step, singleSql + "；影响行数:" + effectRows);
                finalResult = "影响行数" + effectRows;
            } else if (StringUtils.startsWithAny(singleSql.toLowerCase(), SELECT)) {
                try (ResultSet resultSet = statement.executeQuery(singleSql)) {
                    finalResult = resultSetToList(resultSet);
                }
            } else if (StringUtils.startsWith(singleSql.toLowerCase(), INSERT)) {
                statement.executeUpdate(singleSql, Statement.RETURN_GENERATED_KEYS);
                ResultSet keys = statement.getGeneratedKeys();
                finalResult = resultSetToList(keys);
            } else {
                log.warn("SQL操作不支持！sql={}", singleSql);
                throw new ExecuteException("只支持select,update,delete,insert操作！");
            }
        }
        return finalResult;
    }

    @SuppressWarnings("all")
    public static List<Map<String, Object>> resultSetToList(ResultSet rs) throws SQLException {
        if (rs == null) {
            return Collections.EMPTY_LIST;
        } else {
            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();
            ArrayList<Map<String, Object>> list = new ArrayList<>();
            while (rs.next()) {
                HashMap<String, Object> rowData = new HashMap<>(columnCount);
                for (int i = 1; i <= columnCount; i++) {
                    rowData.put(md.getColumnLabel(i), rs.getObject(i));
                }
                list.add(rowData);
            }
            return list;
        }
    }

    @Override
    public StepTypeEnum getStepType() {
        return StepTypeEnum.MYSQL;
    }
}
