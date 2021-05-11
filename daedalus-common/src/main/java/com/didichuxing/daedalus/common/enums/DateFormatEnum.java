package com.didichuxing.daedalus.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

/**
 * @author : jiangxinyu
 * @date : 2020/4/24
 */
@Getter
@AllArgsConstructor
public enum DateFormatEnum {
    TIMESTAMP("Unix时间戳(毫秒)", true, dateTime -> {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(dateTime, formatter).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + "";
    }),
    yyyyMMddHHmmSS("yyyy-MM-dd HH:mm:ss", true, dateTime -> dateTime),
    HHmmSS("HH:mm:ss", false, null),
    yyyyMMdd("yyyy-MM-dd", false, null);


    private String format;

    private boolean isPublic;

    private Function<String, String> converter;

    public boolean isDateTime() {
        return this != HHmmSS && this != yyyyMMdd;
    }


}
