package com.didichuxing.daedalus.service.plugin;

import com.didichuxing.daedalus.common.dto.plugin.UUIDOptions;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author : jiangxinyu
 * @date : 2020/6/17
 */
@Component
public class UUIDPlugin implements Plugin<UUIDOptions> {

    @Override
    public Object compute(UUIDOptions options) {
        return UUID.randomUUID().toString();
    }

    @Override
    public String name() {
        return "UUID";
    }

    @Override
    public String keyword() {
        return "UUID";
    }
}
