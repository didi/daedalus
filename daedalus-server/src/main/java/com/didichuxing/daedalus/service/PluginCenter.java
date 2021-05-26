package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.pojo.ExecuteException;
import com.didichuxing.daedalus.service.plugin.Plugin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
@Service
public class PluginCenter {

    @Autowired
    private List<Plugin> pluginList;


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
