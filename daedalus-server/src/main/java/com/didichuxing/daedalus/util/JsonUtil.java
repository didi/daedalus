package com.didichuxing.daedalus.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.JSONValidator;
import com.didichuxing.daedalus.pojo.ExecuteException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * @author : jiangxinyu
 * @date : 2020/4/30
 */
@UtilityClass
@Slf4j
public class JsonUtil {

    public static String getPath(String obj, String path) {
        try {
            Object extract = JSONPath.extract(obj, path);
            return extract == null ? "" : String.valueOf(extract);
        } catch (Exception e) {
            log.error("根据JSON path获取结果失败", e);
            throw new ExecuteException("json path:" + path + "非法！");
        }
    }

    public static boolean isJson(String string) {
        return JSONValidator.from(string).validate();
    }

    public static String toJsonString(Object obj) {
        return JSON.toJSONString(obj);
    }


    public static void main(String[] args) {
        String ass = "{\"code\":\"SERVICE_RUN_SUCCESS\",\"data\":{\"carBrandChar\":\"B\",\"orderId\":\"\",\"emissionStandards\":\"\",\"carBrandId\":6,\"vehiclePurchaseTaxCertificate\":\"\",\"modifier\":\"system\",\"carFashionId\":1160333,\"hasPublish\":0,\"hasRegisterCertificate\":0,\"cityId\":5,\"pictureConfig\":{\"class\":\"com.xiaoju.automarket.mtn.carcenter.client.model.CarPictureDTO\"},\"plateBelongName\":\"杭州市\",\"hasOnline\":0,\"contact\":\"\",\"id\":445,\"plateNumberColor\":\"\",\"merchantsId\":240,\"mortgageExpiresDate\":\"\",\"hasDrivingLicense\":0,\"mileage\":12,\"gasChangeOrNot\":\"900004002\",\"keyCount\":0,\"displacementIntake\":\"1\",\"modifyDate\":1606793376000,\"carFashionLevel\":\"MPV\",\"storesName\":\"\",\"carBrandName\":\"别克\",\"quotePrice\":8000000,\"energyType\":\"油混\",\"phone\":\"\",\"gearboxType\":1,\"storesId\":3,\"frontMoney\":1000000,\"garageNumber\":\"\",\"status\":1,\"merchantsName\":\"车辆二期商户\",\"plateBelongId\":5,\"color\":\"红色\",\"carModelId\":32838,\"carModelName\":\"GL6\",\"carFashionName\":\"2019款 别克GL6 18T 5座互联豪华型 国V\",\"loginDate\":\"2000-02-01\",\"orderChannel\":1,\"usedType\":5,\"authLevelStatus\":1,\"qaLevel\":1,\"checkStatus\":1,\"carType\":1,\"cityName\":\"杭州市\",\"vinNumber\":\"LVVDC21BXHDUAR4LH\",\"orderPrice\":0,\"class\":\"com.xiaoju.automarket.mtn.carcenter.client.model.CarInfoDTO\",\"createDate\":1606793376000,\"owner\":\"\",\"authLevelId\":1,\"creator\":\"system\",\"authLevelName\":\"认证严选车\",\"saleCount\":0,\"engineNumber\":\"\",\"isLentOut\":0,\"plateNumber\":\"\",\"hasMortgage\":0,\"rfidNumber\":\"\",\"carBrandType\":\"\",\"hasViolation\":0},\"success\":true,\"class\":\"com.xiaoju.automarket.mtn.carcenter.client.response.ResultResponse\",\"status\":200}";
        System.out.println(getPath(ass, "data"));

        Object extract = JSONPath.extract(ass, "data");
        System.out.println(extract);
    }


}
