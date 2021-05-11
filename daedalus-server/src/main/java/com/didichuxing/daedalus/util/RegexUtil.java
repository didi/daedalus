package com.didichuxing.daedalus.util;

import com.didichuxing.daedalus.pojo.BizException;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : jiangxinyu
 * @date : 2020/5/14
 */
@UtilityClass
public class RegexUtil {
    private static final String URL_REGEX = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    private static final String IP_REGEX = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX);
    private static final Pattern IP_PATTERN = Pattern.compile(IP_REGEX);
    private static final Pattern VAR_PATTERN = Pattern.compile("#\\s*\\{([^#{}]*)}");


    public static void validUrl(String url) {
        Matcher matcher = URL_PATTERN.matcher(url);
        if (!matcher.find()) {
            throw new BizException("请输入正确的URL");
        }
    }

    public static void validIp(String ip) {
        Matcher matcher = IP_PATTERN.matcher(ip);
        if (!matcher.find()) {
            throw new BizException("请输入正确的IP地址");
        }
    }

    public static List<String> getVars(String string) {
        Matcher matcher = VAR_PATTERN.matcher(string);
        List<String> vars = new ArrayList<>();
        while (matcher.find()) {
            vars.add(matcher.group(1));
        }
        return vars;
    }

    public static String getVar(String string) {
        Matcher matcher = VAR_PATTERN.matcher(string);
        while (matcher.find()) {
            return matcher.group(1);
        }
        return string;
    }
}
