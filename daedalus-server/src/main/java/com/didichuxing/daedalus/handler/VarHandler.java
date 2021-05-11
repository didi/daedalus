package com.didichuxing.daedalus.handler;

import java.util.regex.Pattern;

/**
 * @author : jiangxinyu
 * @date : 2020/4/20
 */
public class VarHandler {

    private static final String VAR_REGEX = "#\\{([^#{}]+)+}";
    private static final Pattern VAR_PATTERN = Pattern.compile(VAR_REGEX);


}
