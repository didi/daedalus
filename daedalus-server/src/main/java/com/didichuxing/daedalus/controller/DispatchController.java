package com.didichuxing.daedalus.controller;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.enums.ExecTypeEnum;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.service.DispatchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author : jiangxinyu
 * @date : 2020/6/2
 */
@RestController
@RequestMapping("dispatch")
@Api(tags = "调度")
@Slf4j
public class DispatchController {

    @Autowired
    private DispatchService dispatchService;


    @PostMapping("call")
    @ApiOperation("调度运行pipeline")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<ExecuteResult> call(@Valid @RequestBody ExecuteRequest request) {
        log.info("dispatch call request:{}", JSON.toJSONString(request));
        request.setExecType(ExecTypeEnum.NORMAL);
        ExecuteResult result = dispatchService.doDispatch(request);
        Response<ExecuteResult> response = new Response<>();
        if (result != null) {
            response.setSuccess(true);
            response.setCode(ErrorCode.SUCCESS.getCode());
            response.setMsg(result.getExceptionMsg());
        } else {
            response.setSuccess(false);
            response.setMsg("运行失败！");
            response.setCode(ErrorCode.FAILED.getCode());
        }
        response.setData(result);
        return response;
    }


    @PostMapping("callResume")
    @ApiOperation("继续运行失败的")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<ExecuteResult> resume(@RequestParam String logId) {
        ExecuteRequest executeRequest = new ExecuteRequest();
        executeRequest.setResumeLogId(logId);
        executeRequest.setExecType(ExecTypeEnum.RESUME);
        ExecuteResult executeResult = dispatchService.doDispatch(executeRequest);
        return Response.sucResp(executeResult);
    }

    @PostMapping("debug")
    @ApiOperation("debug流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<ExecuteResult> debug(@RequestBody ExecuteRequest debugRequest) {
        debugRequest.setExecType(ExecTypeEnum.DEBUG);
        ExecuteResult executeResult = dispatchService.doDispatch(debugRequest);
        return Response.sucResp(executeResult);
    }
}
