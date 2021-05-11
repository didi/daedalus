package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    @ExceptionHandler
    public Response<Void> handleValidationException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        String errorMsg = bindingResult.getFieldErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining());
        return Response.failResp(ErrorCode.MISS_PARAM.getCode(), errorMsg);
    }

    @ExceptionHandler
    public Response<Void> handleBizException(BizException e) {
        log.error("error!", e);
        return Response.failResp(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler
    public Response<Void> handleBizException(ExecuteException e) {
        log.error("运行流水线error!", e);
        return Response.failResp(-1, e.getMessage());
    }

    @ExceptionHandler
    public Response<Void> handErrorRequest(HttpMessageNotReadableException e) {
        log.error("http请求参数错误!", e);
        return Response.failResp(ErrorCode.ERROR_PARAM.getCode(), e.getMessage());
    }

    @ExceptionHandler
    public Response<Void> handleException(Exception e) {
        log.error("error!", e);
        return Response.failResp(-1, "系统异常！");
    }
}
