package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户相关操作")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("collect")
    @ApiOperation(value = "流水线收藏")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true)
    })
    public Response<Void> collect(@RequestParam @ApiParam("流水线id") String pipelineId,
                                  @RequestParam(defaultValue = "true") @ApiParam(defaultValue = "true") Boolean isCollect) {
        userService.collect(pipelineId, isCollect);
        return Response.sucResp();
    }
}
