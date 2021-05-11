package com.didichuxing.daedalus.dal.adaptor;

import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.dal.EnvDal;
import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import com.didichuxing.daedalus.handler.EnvConverter;
import com.didichuxing.daedalus.util.Context;
import com.didichuxing.daedalus.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author : jiangxinyu
 * @date : 2020/6/24
 */
@Repository
public class EnvDalAdaptor {

    @Autowired
    private EnvDal envDal;

    public Optional<EnvGroupEntity> findById(String envGroupId) {
        if (EnvUtil.isOffline()) {
            EnvGroup envGroup = Context.getRequest().getEnvGroups().get(envGroupId);
            return Optional.ofNullable(EnvConverter.dtoToEntity(envGroup));
        } else {
            return envDal.findById(envGroupId);
        }
    }
}
