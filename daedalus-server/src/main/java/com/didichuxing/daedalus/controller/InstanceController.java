package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.instance.Instance;
import com.didichuxing.daedalus.service.InstanceService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 实例管理
 *
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("instance")
@Api(tags = "实例管理")
public class InstanceController {

    @Autowired
    private InstanceService instanceService;


    @GetMapping("/list")
    @ApiOperation("查询实例列表")
    public Response<List<Instance>> list(@ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                         @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize,
                                         @ApiParam(value = "实例类型", required = true) @RequestParam String insType,
                                         @ApiParam(value = "实例名称") @RequestParam(required = false) String name,
                                         @ApiParam(value = "实例ip") @RequestParam(required = false) String ip) {
        return instanceService.list(insType, page, pageSize, name, ip);
    }

    @GetMapping("/detail")
    @ApiOperation("查询实例详情")
    public Response<Instance> list(@ApiParam(value = "实例id", required = true) @RequestParam String instanceId) {
        return Response.sucResp(instanceService.detail(instanceId));
    }

    @PostMapping("/save")
    @ApiOperation("保存或更新实例,保存时不传id，更新时传id")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> save(@ApiParam(value = "实例", required = true) @RequestBody @Valid Instance instance) {
        instanceService.save(instance);
        return Response.sucResp();
    }


    @PostMapping("/delete")
    @ApiOperation("删除")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> delete(@ApiParam(value = "实例id", required = true) @RequestParam String instanceId) {
        instanceService.delete(instanceId);
        return Response.sucResp();
    }

}
