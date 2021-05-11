package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.service.plugin.Plugin;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.runtime.type.AviatorFunction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
@Service
public class PluginCenter {

    @Autowired
    private List<Plugin> pluginList;
    @Autowired
    private List<AviatorFunction> functionList;

    @PostConstruct
    public void init() {
        functionList.forEach(AviatorEvaluator::addFunction);
    }

    public boolean isPlugin(String varName) {
        return pluginList.stream()
                .anyMatch(plugin -> plugin.keyword().equals(varName));
    }

    public Object compute(String variable) {
        return pluginList.stream()
                .filter(plugin -> plugin.keyword().equals(variable))
                .findFirst()
                .orElseThrow(() -> new ExecuteException("Plugin不存在！"))
                .compute(null);
    }
}
