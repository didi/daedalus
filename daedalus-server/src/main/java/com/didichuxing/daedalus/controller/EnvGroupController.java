package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.service.EnvService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/15
 */
@RestController
@RequestMapping("env")
@Api(tags = "环境管理")
public class EnvGroupController {


    @Autowired
    private EnvService envService;


    @GetMapping("/list")
    @ApiOperation("查询列表")
    public Response<List<EnvGroup>> list(@ApiParam(value = "搜索的环境组名字", required = false) @RequestParam(required = false) String name,
                                         @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                         @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return envService.list(name, page, pageSize);
    }

    @GetMapping("/detail")
    @ApiOperation("查询详情")
    public Response<EnvGroup> detail(@ApiParam(value = "环境组Id", required = true) @RequestParam String envGroupId) {
        return Response.sucResp(envService.detail(envGroupId));
    }

    @PostMapping("/save")
    @ApiOperation("保存或更新环境组,保存时不传id")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> save(@ApiParam(value = "环境组", required = true) @RequestBody @Valid EnvGroup envGroup) {
        envService.save(envGroup);
        return Response.sucResp();
    }


    @PostMapping("/deleteEnv")
    @ApiOperation("删除环境")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> delete(@ApiParam(value = "环境组Id", required = true) @RequestParam String envGroupId) {
        envService.delete(envGroupId);
        return Response.sucResp();
    }


}
