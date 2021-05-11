package com.didichuxing.daedalus.controller;

import com.didichuxing.daedalus.aop.TimeLog;
import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.BizLine;
import com.didichuxing.daedalus.common.enums.BizLineEnum;
import com.didichuxing.daedalus.common.enums.DateFormatEnum;
import com.didichuxing.daedalus.common.enums.InputTypeEnum;
import com.didichuxing.daedalus.common.enums.OperatorEnum;
import com.didichuxing.daedalus.common.enums.dubbo.DubboParamType;
import com.didichuxing.daedalus.common.enums.redis.RedisOperationType;
import com.didichuxing.daedalus.service.ConfigService;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Protocol;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/4/23
 */
@RestController
@RequestMapping("/config")
@Api(tags = "配置查询")
public class ConfigController {

    @Autowired
    private ConfigService configService;


    @ApiOperation("业务线")
    @GetMapping("/bizLine")
    public Response<List<BizLine>> getBizLines() {

        List<BizLine> bizLines = Arrays.stream(BizLineEnum.values())
                .map(bizLineEnum -> new BizLine(bizLineEnum.getCode(), bizLineEnum.getBizName()))
                .collect(Collectors.toList());
        return Response.sucResp(bizLines);

    }

    @ApiOperation("日期格式")
    @GetMapping("/dateFormat")
    @TimeLog
    public Response<List<ImmutableMap<String, String>>> dateFormat() {

        List<ImmutableMap<String, String>> formats = Arrays.stream(DateFormatEnum.values())
                .filter(DateFormatEnum::isPublic)
                .map(format -> ImmutableMap.of("name", format.name(), "desc", format.getFormat()))
                .collect(Collectors.toList());
        return Response.sucResp(formats);

    }

    @ApiOperation("标签推荐")
    @GetMapping("/tags")
    public Response<List<String>> suggestTags(@ApiParam(required = false, value = "搜索tag") @RequestParam(required = false) String searchKey) {
        return Response.sucResp(configService.tagSuggest(searchKey));
    }

    @ApiOperation("条件操作符")
    @GetMapping("/operator")
    public Response<List<ImmutableMap<String, String>>> operator() {
        List<ImmutableMap<String, String>> ret = Arrays.stream(OperatorEnum.values())
                .map(operator -> ImmutableMap.of("name", operator.name(), "desc", operator.getDesc()))
                .collect(Collectors.toList());

        return Response.sucResp(ret);
    }

    @ApiOperation("Dubbo参数类型")
    @GetMapping("/dubboParamType")
    public Response<List<ImmutableMap<String, String>>> dubboParamType() {
        List<ImmutableMap<String, String>> ret = Arrays.stream(DubboParamType.values())
                .map(dubboParamType -> ImmutableMap.of("name", dubboParamType.getClassName(), "value", dubboParamType.getClassName()))
                .collect(Collectors.toList());

        return Response.sucResp(ret);
    }

    @ApiOperation("输入组件")
    @GetMapping("/inputType")
    public Response<List<ImmutableMap<String, String>>> inputType() {
        List<ImmutableMap<String, String>> ret = Arrays.stream(InputTypeEnum.values())
                .map(type -> ImmutableMap.of("name", type.name(), "desc", type.getDesc()))
                .collect(Collectors.toList());

        return Response.sucResp(ret);
    }

    @ApiOperation("redis操作类型")
    @GetMapping("/redisCommand")
    public Response<List<String>> redisCommand() {
        List<String> ret = Arrays.stream(RedisOperationType.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        return Response.sucResp(ret);
    }
}
