package com.didichuxing.daedalus.util;

import com.didichuxing.daedalus.common.enums.OperatorEnum;
import com.didichuxing.daedalus.entity.step.additional.ConditionEntity;
import com.didichuxing.daedalus.entity.step.additional.RuleEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author : jiangxinyu
 * @date : 2020/5/6
 */
@Slf4j
@UtilityClass
public class RuleUtil {

    /**
     * 条件执行
     * 如果是变量的话需要用#{}，否则
     *
     * @param rule
     * @param vars
     * @param logConsumer
     * @return
     */
    private static boolean executeRule(RuleEntity rule, Map<String, String> vars, Consumer<String> logConsumer) {
        if (rule == null || rule.getOperator() == null || rule.getVariable() == null || rule.getValue() == null) {
            throw new ExecuteException("rule为空！");
        }
        OperatorEnum operator = rule.getOperator();
        String expression;
        if (operator == OperatorEnum.IS || operator == OperatorEnum.IS_NOT) {
            //组装字符串相等
            expression = "'" + rule.getVariable() + "'" + operator.getOperator() + "'" + rule.getValue() + "'";
        } else {
            expression = rule.getVariable() + operator.getOperator() + rule.getValue();
        }
        logConsumer.accept("条件表达式:" + expression);
        log.info("条件表达式:{}", expression);
        Map<String, Object> env = vars == null ? new HashMap<>() : new HashMap<>(vars);
        if (containVar(expression)) {
            throw new ExecuteException(String.format("变量%s缺失！", RegexUtil.getVars(expression)));
        }
        //TODO 表达式判断
        return false;

    }


    public static boolean condition(ConditionEntity condition, Map<String, String> vars, Consumer<String> logConsumer) {
        if (condition == null || CollectionUtils.isEmpty(condition.getRules())) {
            return true;
        }
        RuleEntity ruleEntity = condition.getRules().get(0);
        return executeRule(ruleEntity, vars, logConsumer);
    }


    /**
     * 是否包含#{}的变量
     *
     * @param val
     * @return
     */
    private static boolean containVar(String val) {
        return val.contains("#{") && val.contains("}");
    }
}
