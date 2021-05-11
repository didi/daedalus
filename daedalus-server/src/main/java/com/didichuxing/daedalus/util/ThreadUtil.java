package com.didichuxing.daedalus.util;

import lombok.experimental.UtilityClass;

/**
 * @author : jiangxinyu
 * @date : 2020/4/28
 */
@UtilityClass
public class ThreadUtil {

    public void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {

        }
    }
}
