package com.didichuxing.daedalus.service.plugin;

import com.didichuxing.daedalus.common.dto.plugin.Options;

import javax.validation.constraints.NotNull;

/**
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
public interface Plugin<T extends Options> {


    /**
     * 插件逻辑
     *
     * @param options
     * @return
     */
    @NotNull
    Object compute(T options);

    /**
     * @return 插件名
     */
    String name();


    /**
     * @return 变量名
     */
    String keyword();
}
