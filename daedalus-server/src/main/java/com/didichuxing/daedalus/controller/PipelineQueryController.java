package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.pipeline.PipelineInfo;
import com.didichuxing.daedalus.service.PipelineQueryService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("pipeline")
@Api(tags = "流水线查询")
public class PipelineQueryController {

    @Autowired
    private PipelineQueryService pipelineQueryService;


    @GetMapping("queryRecently")
    @ApiOperation(value = "查询最近使用的流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataTypeClass = String.class, defaultValue = "jiangxinyu")
    })
    public PageResponse<List<PipelineInfo>> queryRecently(@ApiParam(value = "业务线") @RequestParam(required = false) List<Integer> bizLine,
                                                          @ApiParam(value = "标签") @RequestParam(required = false) List<String> tags,
                                                          @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                          @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.queryRecentUsed(bizLine, tags, page, pageSize);
    }

    @GetMapping("list")
    @ApiOperation(value = "所有流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public PageResponse<List<PipelineInfo>> list(@ApiParam(value = "业务线") @RequestParam(required = false) List<Integer> bizLine,
                                                 @ApiParam(value = "标签") @RequestParam(required = false) List<String> tags,
                                                 @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                 @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.list(bizLine, tags, page, pageSize);
    }

    @GetMapping("queryOwn")
    @ApiOperation(value = "查询自己的流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public PageResponse<List<PipelineInfo>> queryByCreator(@ApiParam(value = "业务线") @RequestParam(required = false) List<Integer> bizLine,
                                                           @ApiParam(value = "标签") @RequestParam(required = false) List<String> tags,
                                                           @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                           @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.queryByCreator(bizLine, tags, page, pageSize);
    }

    @GetMapping("queryFavorites")
    @ApiOperation(value = "查询收藏夹")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<List<PipelineInfo>> queryFavorites(@ApiParam(value = "业务线") @RequestParam(required = false) List<Integer> bizLine,
                                                       @ApiParam(value = "标签") @RequestParam(required = false) List<String> tags,
                                                       @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                       @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.queryFavorites(bizLine, tags, page, pageSize);
    }

    @GetMapping("queryPopular")
    @ApiOperation(value = "查询使用最多的流水线")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<List<PipelineInfo>> queryPopular(@ApiParam(value = "业务线") @RequestParam(required = false) List<Integer> bizLine,
                                                     @ApiParam(value = "标签") @RequestParam(required = false) List<String> tags,
                                                     @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                     @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.queryPopular(bizLine, tags, page, pageSize);
    }

    @GetMapping("search")
    @ApiOperation(value = "搜索")
    public Response<List<PipelineInfo>> search(@ApiParam(value = "搜索关键词") @RequestParam(required = false) String key) {
        return Response.sucResp(pipelineQueryService.search(key));
    }

    @GetMapping("byTag")
    @ApiOperation(value = "通过tag搜索")
    public Response<List<PipelineInfo>> searchByTag(@ApiParam(value = "标签") @RequestParam(required = true) List<String> tags,
                                                    @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                    @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.searchByTag(tags, page, pageSize);
    }
    @GetMapping("byCreator")
    @ApiOperation(value = "通过创建人搜索")
    public Response<List<PipelineInfo>> searchByTag(@ApiParam(value = "creator") @RequestParam(required = true) String creator,
                                                    @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                                    @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return pipelineQueryService.searchByCreator(creator, page, pageSize);
    }

    @GetMapping("permit")
    @ApiOperation(value = "是否允许")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", name = "username", required = true, dataType = "String", defaultValue = "jiangxinyu"),
            @ApiImplicitParam(paramType = "header", name = "usernamezh", value = "中文用户名", required = true, dataType = "String", defaultValue = "jiangxinyu")
    })
    public Response<Boolean> permit(@ApiParam(value = "pipelineId") @RequestParam() String pipelineId) {
        return Response.sucResp(pipelineQueryService.permit(pipelineId));
    }
}
