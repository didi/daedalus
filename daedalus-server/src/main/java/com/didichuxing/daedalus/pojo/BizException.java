package com.didichuxing.daedalus.pojo;

import com.didichuxing.daedalus.common.ErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author : jiangxinyu
 * @date : 2020/3/23
 */
public class BizException extends RuntimeException {

    @Getter
    @Setter
    private int errorCode;

    public BizException(String message) {
        super(message);
        this.errorCode = -1;
    }

    public BizException(Throwable cause) {
        super(cause);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.getDesc());
        this.errorCode = errorCode.getCode();
    }
}
