package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.PageResponse;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.log.Log;
import com.didichuxing.daedalus.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/3/24
 */
@RestController
@RequestMapping("log")
@Api(tags = "流水线运行记录")
public class LogController {

    @Autowired
    private LogService logService;

    @GetMapping("list")
    @ApiOperation("查询日志")
    public PageResponse<List<Log>> list(@ApiParam(value = "流水线id", required = true) @RequestParam String pipelineId,
                                        @ApiParam(value = "页数,从0开始", required = true, defaultValue = "0") @RequestParam(defaultValue = "0") int page,
                                        @ApiParam(value = "每页大小", required = true, defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        return logService.list(pipelineId, page, pageSize);
    }


    @GetMapping("detail")
    @ApiOperation("查询日志")
    public Response<Log> logDetail(@ApiParam(value = "log id", required = true) @RequestParam String logId) {
        return Response.sucResp(logService.logDetail(logId));
    }


    @PostMapping("insert")
    @ApiOperation("插入日志")
    public Response<String> insert(@RequestBody Log log) {
        Response<String> response = Response.sucResp();
        response.setData(logService.insertLog(log));
        return response;
    }
}
