package com.didichuxing.daedalus.common;

import com.fasterxml.jackson.databind.util.StdConverter;

/**
 * 脱敏
 */
public class DesensitizeConverter extends StdConverter<String, String> {

    @Override
    public String convert(String value) {
        return "******";
    }

}


