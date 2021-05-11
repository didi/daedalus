package com.didichuxing.daedalus.util;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.pojo.BizException;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@UtilityClass
public class Validator {

    public void notBlank(String str, ErrorCode errorCode) {
        if (StringUtils.isBlank(str)) {
            throw new BizException(errorCode);
        }
    }

    public void notNull(Object obj, ErrorCode errorCode) {
        if (obj == null) {
            throw new BizException(errorCode);
        }
    }

    public void equals(Object o1, Object o2, String msg) {
        if (!Objects.equals(o1, o2)) {
            throw new BizException(msg);

        }
    }

    public static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new BizException(message);
        }
    }

    public static void valid(Object obj, Class... groups) {
        Set<ConstraintViolation<Object>> validate = Validation.buildDefaultValidatorFactory().getValidator().validate(obj, groups);

        validate.forEach(v -> {
            throw new BizException(v.getMessage());
        });

    }


}
