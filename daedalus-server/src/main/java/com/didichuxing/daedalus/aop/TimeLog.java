package com.didichuxing.daedalus.aop;

import java.lang.annotation.*;

/**
 * @author : jiangxinyu
 * @date : 2020/5/8
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TimeLog {
}
