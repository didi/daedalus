package com.didichuxing.daedalus.service.plugin;

import com.didichuxing.daedalus.common.dto.plugin.Options;
import org.springframework.stereotype.Component;

/**
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
@Component
public class TimestampPlugin implements Plugin<Options> {

    @Override
    public Object compute(Options options) {
        return System.currentTimeMillis() + "";
    }

    @Override
    public String name() {
        return "timestamp";
    }

    @Override
    public String keyword() {
        return "timestamp";
    }
}
