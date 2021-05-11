package com.didichuxing.daedalus.service.plugin.function;

import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Component
public class TimestampFunction extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        return new AviatorString(System.currentTimeMillis() + "");
    }

    @Override
    public String getName() {
        return "timestamp";
    }
}
