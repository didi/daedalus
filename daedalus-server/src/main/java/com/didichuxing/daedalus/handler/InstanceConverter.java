package com.didichuxing.daedalus.handler;

import com.didichuxing.daedalus.common.dto.instance.Instance;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import lombok.experimental.UtilityClass;

/**
 * @author : jiangxinyu
 * @date : 2020/4/16
 */
@UtilityClass
public class InstanceConverter {

    public static InstanceEntity dtoToEntity(Instance instance) {
        if (instance == null) {
            return null;
        }
        InstanceEntity instanceEntity = SimpleConverter.convert(instance, InstanceEntity.class);
        instanceEntity.setCookies(instance.getCookies());
        instanceEntity.setFormData(instance.getFormData());
        instanceEntity.setHeaders(instance.getHeaders());
        instanceEntity.setUrlParams(instance.getUrlParams());
        return instanceEntity;
    }

    public static Instance entityToDto(InstanceEntity instanceEntity) {
        if (instanceEntity == null) {
            return null;
        }
        Instance instance = SimpleConverter.convert(instanceEntity, Instance.class);
        instance.setCookies(instanceEntity.getCookies());
        instance.setFormData(instanceEntity.getFormData());
        instance.setHeaders(instanceEntity.getHeaders());
        instance.setUrlParams(instanceEntity.getUrlParams());
        return instance;
    }
}
