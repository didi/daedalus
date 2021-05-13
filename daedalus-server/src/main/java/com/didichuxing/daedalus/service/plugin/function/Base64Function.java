package com.didichuxing.daedalus.service.plugin.function;

import com.didichuxing.daedalus.pojo.ExecuteException;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Component
public class Base64Function extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        if (ArrayUtils.getLength(args) > 0) {
            return new AviatorString(Base64.getEncoder().encodeToString(String.valueOf(args[0].getValue(env)).getBytes(StandardCharsets.UTF_8)));
        }
        throw new ExecuteException("base64参数数量异常！");
    }

    @Override
    public String getName() {
        return "base64";
    }
}
