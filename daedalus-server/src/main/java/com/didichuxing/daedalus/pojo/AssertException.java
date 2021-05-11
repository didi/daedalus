package com.didichuxing.daedalus.pojo;

/**
 * @author : jiangxinyu
 * @date : 2021/1/7
 */
public class AssertException extends ExecuteException {
    public AssertException(String message, Throwable cause) {
        super(message, cause);
    }

    public AssertException(String message) {
        super(message);
    }
}
