package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
import com.didichuxing.daedalus.service.PipelineManageService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */
@RestController
@RequestMapping("/pipeline")
@Api(tags = "流水线管理")
@Slf4j
public class PipelineManageController {

    @Autowired
    private PipelineManageService pipelineManageService;

    @PostMapping("create")
    @ApiOperation(value = "创建流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<String> create(@Valid @RequestBody Pipeline pipeline) {
        log.info("创建pipeline请求:{}", pipeline);
        Pipeline result = pipelineManageService.create(pipeline);
        return Response.sucResp("创建成功！", result.getId());
    }

    @PostMapping("copy")
    @ApiOperation(value = "复制流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<String> copy(@RequestParam @ApiParam("流水线id") String pipelineId) {
        return Response.sucResp("复制成功！", pipelineManageService.copy(pipelineId).getId());
    }

    @GetMapping("detail")
    @ApiOperation(value = "流水线详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Pipeline> detail(@RequestParam @ApiParam("流水线id") String pipelineId) {
        return Response.sucResp(pipelineManageService.detail(pipelineId));
    }

    @PostMapping("update")
    @ApiOperation(value = "修改流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> update(@Valid @RequestBody Pipeline pipeline) {
        log.info("修改pipeline请求:{}", pipeline);
        pipelineManageService.update(pipeline);
        return Response.sucResp();
    }

    @PostMapping("delete")
    @ApiOperation(value = "删除流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> delete(@RequestParam String pipelineId) {
        pipelineManageService.delete(pipelineId);
        return Response.sucResp();
    }


}
