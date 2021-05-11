package com.didichuxing.daedalus.pojo;

/**
 * pipeline 运行异常
 *
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
public class ExecuteException extends RuntimeException {
    public ExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecuteException(String message) {
        super(message);
    }
}
