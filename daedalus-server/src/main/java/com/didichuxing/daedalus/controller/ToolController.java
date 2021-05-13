package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.step.HttpStep;
import com.didichuxing.daedalus.pojo.BizException;
import com.didichuxing.daedalus.util.EnvUtil;
import com.didichuxing.daedalus.util.SwaggerUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/29
 */
@RestController
@RequestMapping("tool")
@Api(tags = "工具")
public class ToolController {


    @GetMapping("swagger")
    @ApiOperation(value = "swagger解析")
    public Response<List<HttpStep>> swagger(@RequestParam @ApiParam("服务地址") String url) {
        if (!url.toLowerCase().startsWith("http://")) {
            throw new BizException("Url必须以http开头！");
        }
        if (EnvUtil.isOffline() || EnvUtil.isTest()) {
            return Response.sucResp(SwaggerUtil.parseSwagger(url));
        }
        throw new BizException("只能线下环境使用！");
    }


}
