package com.didichuxing.daedalus.util;

import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

/**
 * @author : jiangxinyu
 * @date : 2020/6/29
 */
@UtilityClass
public class EnvUtil {

    public static boolean isOffline() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return "offline".equals(activeProfile);
    }

    public static boolean isTest() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return StringUtils.isBlank(activeProfile) || "test".equals(activeProfile);
    }

    public static boolean isOnline() {
        String activeProfile = System.getProperty("spring.profiles.active");
        return "prod".equals(activeProfile) || "pre".equals(activeProfile);
    }


}
