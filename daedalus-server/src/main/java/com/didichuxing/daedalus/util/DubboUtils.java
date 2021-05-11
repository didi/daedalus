package com.didichuxing.daedalus.util;

import com.alibaba.fastjson.JSON;
import com.didichuxing.daedalus.common.dto.Pair;
import com.didichuxing.daedalus.common.dto.step.additional.DubboParam;
import com.didichuxing.daedalus.common.enums.dubbo.DubboParamType;
import com.didichuxing.daedalus.pojo.ExecuteException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.service.GenericService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2019/12/5
 */
@UtilityClass
@Slf4j
public class DubboUtils {

    private ApplicationConfig applicationConfig;

    static {
        applicationConfig = new ApplicationConfig();
        applicationConfig.setName("daedalus");
        applicationConfig.setEnvironment("test");
    }


    /**
     * @param interfaceName 必传
     * @param methodName    必传
     * @param registries    注册中心配置，使用注册中心时传，直连传null
     * @param group         group 没有传null
     * @param version       没有传null
     * @param ipWithPort    直连使用，provider的 ip:port
     * @param dubboParams   参数
     * @return
     */
    public static Object invokeGeneric(String interfaceName, String methodName, List<RegistryConfig> registries, String group, String version, String ipWithPort, List<DubboParam<String, String>> dubboParams, List<Pair<String, Object>> attachments) {
        ReferenceConfig<GenericService> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
//        reference.setFilter("XDAFilter");
        if (registries != null) {
            reference.setRegistries(registries); // 多个注册中心可以用setRegistries()
        }
        if (StringUtils.isNotBlank(ipWithPort)) {
            reference.setUrl("dubbo://" + ipWithPort);
        }
        reference.setInterface(interfaceName);
        if (StringUtils.isNotBlank(version)) {
            reference.setVersion(version);
        }
        if (StringUtils.isNotBlank(group)) {
            reference.setGroup(group);
        }
        if (CollectionUtils.isNotEmpty(attachments)) {
            RpcContext.getContext().getObjectAttachments().putAll(attachments.stream().collect(Collectors.toMap(Pair::getName, Pair::getValue)));
        }
        // 声明为泛化接口
        reference.setGeneric(true);
        reference.setTimeout(10000);
        reference.setReconnect("false");

        GenericService genericService = reference.get();
        String[] paramTypes = null;
        Object[] params = null;
        if (CollectionUtils.isNotEmpty(dubboParams)) {
            paramTypes = dubboParams.stream().map(DubboParam::getType).toArray(String[]::new);
            params = dubboParams.stream().map(dubboParam -> {
                String clazz = dubboParam.getType();
                if (clazz.equals(Map.class.getName())) {
                    try {
                        return new ObjectMapper().readValue(dubboParam.getValue(), Map.class);
                    } catch (JsonProcessingException e) {
                        log.error("parse json to map failed!", e);
                        throw new ExecuteException(dubboParam.getValue() + "不是合法的JSON！");
                    }
                } else if (clazz.equals(List.class.getName())) {
                    try {
                        return new ObjectMapper().readValue(dubboParam.getValue(), List.class);
                    } catch (JsonProcessingException e) {
                        log.error("parse json to List failed!", e);
                        throw new ExecuteException(dubboParam.getValue() + "不是合法的JSON！");
                    }
                } else if (DubboParamType.isPrimitiveWarp(clazz)) {
                    return dubboParam.getValue();
                } else if (DubboParamType.isPrimitiveBasic(clazz)) {
                    //基本类型需要转
                    return DubboParamType.getType(clazz).getConverter().apply(dubboParam.getValue());
                } else {
                    //复杂对象
//                    return JSON.parseObject(dubboParam.getValue(), Map.class);
                    try {
                        return new ObjectMapper().readValue(dubboParam.getValue(), Map.class);
                    } catch (JsonProcessingException e) {
                        log.error("parse json to map failed!", e);
                        throw new ExecuteException(dubboParam.getValue() + "不是合法的JSON！");
                    }
                }
            }).toArray();
        }
        try {
            return genericService.$invoke(methodName, paramTypes, params);
        } finally {
            //调用时清除
//            RpcContext.removeContext();
//            RpcContext.removeServerContext();
        }
    }

    public static void main(String[] args) throws JsonProcessingException {
        String ss = "[\n" +
                "    {\n" +
                "        \"aa\": \"bb\",\n" +
                "        \"ab\": {\n" +
                "            \"cc\": 233\n" +
                "        }\n" +
                "    }\n" +
                "]";

        String sss = "[1,2,3,4]";


        List list2 = JSON.parseObject(ss, List.class);
        List list3 = JSON.parseObject(sss, List.class);

        List<DubboParam<String, String>> params = new ArrayList<>();
        params.add(DubboParam.of("com.xiaoju.automarket.car.bill.entity.request.BillFeeRemissionRequest", "{\"sign\":\"QWERTYUIOPASDFGHJKLZXCVBNM-test\",\"billId\":\"288230376152061811\",\"remissionItems\":[{\"feeItem\":\"112\",\"amount\":\"2999\"}]}"));


//        Object ret = invokeGeneric("com.xiaoju.automarket.car.bill.provider.BillService ", "billFeeRemission", null, null, "1.0.0", "127.0.0.1:20881", params);
//        System.out.println(JSON.toJSONString(ret));


        List list = new ObjectMapper().readValue(ss, List.class);

        List list1 = new ObjectMapper().readValue(sss, List.class);

        System.out.println(list);
    }
}
