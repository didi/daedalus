package com.didichuxing.daedalus.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

import static com.didichuxing.daedalus.common.ErrorCode.SUCCESS;


/**
 * @author jiangxinyu
 */
@Data
@ApiModel
public class Response<T> implements Serializable {

    /**
     * Define long serialVersionUID
     */
    private static final long serialVersionUID = 3928402875296079225L;
    /**
     * 状态
     */
    private boolean success;
    /**
     * 状态码 例如:0-正常；-1-系统异常
     */
    private int code;

    /**
     * 状态消息
     */
    private String msg;


    @ApiModelProperty("返回的数据")
    private T data;


    public static Response<Void> failResp(int code, String message) {
        Response<Void> response = new Response<>();
        response.setCode(code);
        response.setMsg(message);
        return response;
    }

    public static <T> Response<T> sucResp(String message, T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setCode(SUCCESS.getCode());
        response.setMsg(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> sucResp(T data) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setCode(SUCCESS.getCode());
        response.setMsg("success");
        response.setData(data);
        return response;
    }

    public static <T> Response<T> sucResp(String message) {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setCode(SUCCESS.getCode());
        response.setMsg(message);
        return response;
    }

    public static <T> Response<T> sucResp() {
        Response<T> response = new Response<>();
        response.setSuccess(true);
        response.setCode(SUCCESS.getCode());
        response.setMsg(SUCCESS.getDesc());
        return response;
    }

}