package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.directory.Directory;
import com.didichuxing.daedalus.service.DirectoryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author : jiangxinyu
 * @date : 2020/4/15
 */
@RestController
@RequestMapping("directory")
@Api(tags = "目录")
public class DirectoryController {


    @Autowired
    private DirectoryService directoryService;


    @GetMapping("/detail")
    @ApiOperation("查询详情")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Directory> detail() {
        return Response.sucResp(directoryService.detail());
    }

    @PostMapping("/save")
    @ApiOperation("保存或更新")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> save(@ApiParam(value = "环境组", required = true) @RequestBody @Valid Directory directory) {
        directoryService.save(directory);
        return Response.sucResp();
    }

    @GetMapping("/getLinkId")
    @ApiOperation("获取分享链接")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<String> getLinkId(@RequestParam String nodeId) {
        String linkId = directoryService.getLinkId(nodeId);
        return Response.sucResp("success!", linkId);
    }

    @GetMapping("/importShare")
    @ApiOperation("导入分享")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Void> importShare(@RequestParam String linkId) {
        directoryService.importShare(linkId);
        return Response.sucResp();
    }


}
