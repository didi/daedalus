package com.didichuxing.daedalus.client;

import lombok.Data;

/**
 * @author : jiangxinyu
 * @date : 2020/9/24
 */
@Data
public class Response {

    private ResponseData data;

    private boolean success;

    private String msg;


    @Data
    public static class ResponseData {
        private String result;

    }
}
