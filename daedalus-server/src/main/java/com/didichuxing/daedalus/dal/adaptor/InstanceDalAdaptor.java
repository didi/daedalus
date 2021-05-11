package com.didichuxing.daedalus.dal.adaptor;

import com.didichuxing.daedalus.common.enums.InstanceTypeEnum;
import com.didichuxing.daedalus.dal.InstanceDal;
import com.didichuxing.daedalus.entity.instance.InstanceEntity;
import com.didichuxing.daedalus.handler.InstanceConverter;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author : jiangxinyu
 * @date : 2020/6/24
 */
@Repository
public class InstanceDalAdaptor {
    @Autowired
    private InstanceDal instanceDal;

    public InstanceEntity findByIdAndType(String instanceId, InstanceTypeEnum registry) {
        if (EnvUtil.isOffline()) {
            return InstanceConverter.dtoToEntity(Context.getRequest().getInstances().get(instanceId));
        } else {
            return instanceDal.findByIdAndType(instanceId, registry);
        }
    }
}
