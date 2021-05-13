package com.didichuxing.daedalus.service.plugin.function;

import com.didichuxing.daedalus.pojo.ExecuteException;
import com.googlecode.aviator.runtime.function.AbstractVariadicFunction;
import com.googlecode.aviator.runtime.type.AviatorObject;
import com.googlecode.aviator.runtime.type.AviatorString;
import com.googlecode.aviator.utils.Utils;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/9/28
 */
@Component
public class MD5Function extends AbstractVariadicFunction {
    @Override
    public AviatorObject variadicCall(Map<String, Object> env, AviatorObject... args) {
        if (ArrayUtils.getLength(args) > 0) {

            return new AviatorString(Utils.md5sum(String.valueOf(args[0].getValue(env))));
        }
        throw new ExecuteException("md5参数数量异常！");
    }

    @Override
    public String getName() {
        return "md5";
    }
}
