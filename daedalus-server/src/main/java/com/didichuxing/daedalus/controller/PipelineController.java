package com.didichuxing.daedalus.controller;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.ErrorCode;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.pojo.request.ExecuteRequest;
import com.didichuxing.daedalus.pojo.request.ExecuteResult;
import com.didichuxing.daedalus.service.PipelineService;
import com.didichuxing.daedalus.util.Context;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("/pipeline")
@Api(tags = "流水线运行")
@Slf4j
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    /**
     * 执行pipeline，现在有dispatch controller执行
     *
     * @param request
     * @return
     */
    @PostMapping("execute")
    @ApiOperation("运行pipeline(线下使用)")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<ExecuteResult> execute(@Valid @RequestBody ExecuteRequest request) {
        log.info("pipeline execute request:{}", JSON.toJSONString(request));

        Context.setRequest(request);
        ExecuteResult result = pipelineService.execute(request);
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


}
