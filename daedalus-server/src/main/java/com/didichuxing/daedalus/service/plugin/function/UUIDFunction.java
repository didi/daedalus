package com.didichuxing.daedalus.service.plugin.function;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Component
public class UUIDFunction extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        return new AviatorString(UUID.randomUUID().toString());
    }

    @Override
    public String getName() {
        return "uuid";
    }
}
