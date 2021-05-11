//package com.didichuxing.daedalus.pojo.request;
//
//import com.didichuxing.daedalus.common.dto.pipeline.Pipeline;
//import io.swagger.annotations.ApiModelProperty;
//import lombok.Data;
//
//import javax.validation.constraints.NotNull;
//import java.io.Serializable;
//import java.util.Map;
//
///**
// * @author : jiangxinyu
// * @date : 2021/1/20
// */
//@Data
//public class DebugRequest implements Request {
//
//    @NotNull(message = "流水线不能为空！")
//    @ApiModelProperty("流水线")
//    private Pipeline pipeline;
//
//    @ApiModelProperty("运行时用户输入")
//    private Map<String, String> inputs;
//
//    @ApiModelProperty("环境")
//    private String env;
//}
