package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.entity.step.additional.AssertEntity;
import com.didichuxing.daedalus.entity.step.additional.ConditionEntity;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 变量处理
 *
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
@Service
@Slf4j
public class VariableProcessor {

    @Autowired
    protected PluginCenter pluginCenter;

    private static final Pattern PATTERN = Pattern.compile("#\\s*\\{([^#{}]*)}");

    public void process(Object obj, Map<String, String> vars) {
        if (obj == null) {
            return;
        }
        Field[] fields = FieldUtils.getAllFields(obj.getClass());
        for (Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = ReflectionUtils.getField(field, obj);
            if (fieldValue instanceof String) {
                processString(field, obj, vars);
            } else if (fieldValue instanceof List) {
                processList(field, (List) fieldValue, vars);
            } else if (fieldValue instanceof Map) {
                processMap(field, (Map) fieldValue, vars);
            } else if (fieldValue instanceof ConditionEntity) {
                process(fieldValue, vars);
            }

        }
    }

    @SuppressWarnings("unchecked")
    private void processMap(Field field, Map fieldValue, Map<String, String> vars) {
        if (fieldValue == null) {
            return;
        }
        fieldValue.forEach((key, value) -> {
            if (value instanceof String) {
                String newValue = doProcess((String) value, vars);
                fieldValue.put(key, newValue);
            }
        });

    }


    private void processString(Field field, Object obj, Map<String, String> vars) {
        String value = (String) ReflectionUtils.getField(field, obj);
        value = doProcess(value, vars);
        ReflectionUtils.setField(field, obj, value);
    }


    @SuppressWarnings("all")
    private void processList(Field field, List fieldValue, Map<String, String> vars) {
        if (CollectionUtils.isEmpty(fieldValue)) {
            return;
        }
        for (Object value : fieldValue) {
            if (value instanceof AssertEntity) {
                return;
            }
            process(value, vars);
        }


    }


    /**
     * 替换变量
     *
     * @param content
     * @param vars
     * @return
     */
    String doProcess(String content, Map<String, String> vars) {
        StringBuilder stringBuilder = new StringBuilder(content);
        Matcher matcher = PATTERN.matcher(content);
        while (matcher.find()) {
            String group = matcher.group();
            String placeholder = matcher.group(1);
            int start = stringBuilder.indexOf(group);
            int end = start + group.length();
            processVariable(stringBuilder, start, end, placeholder, vars);
        }
        return stringBuilder.toString();
    }


    void processVariable(StringBuilder stringBuilder, int start, int end, String variable, Map<String, String> variables) {
        if (pluginCenter.isPlugin(variable)) {
            Object computeRet = pluginCenter.compute(variable);
            stringBuilder.replace(start, end, computeRet.toString());
        } else if (variables.get(variable) != null) {
            Object realValue = variables.get(variable);
            if (realValue instanceof String || ClassUtils.isPrimitiveOrWrapper(realValue.getClass())) {
                String varValue = String.valueOf(realValue);
                if (StringUtils.isNotBlank(varValue)) {
                    stringBuilder.replace(start, end, varValue);
                }
            } else {
                throw new ExecuteException("变量" + variable + "的值请只使用基本类型！不支持对象或数组！");
            }
        } else {
            log.warn("函数不存在，表达式执行失败！");
            throw new ExecuteException("函数 " + variable + "不存在！");


        }

    }

}

