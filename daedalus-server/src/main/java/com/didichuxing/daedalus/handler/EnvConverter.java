package com.didichuxing.daedalus.handler;

import com.didichuxing.daedalus.common.dto.env.EnvGroup;
import com.didichuxing.daedalus.common.enums.ClusterEnum;
import com.didichuxing.daedalus.entity.env.EnvGroupEntity;
import lombok.experimental.UtilityClass;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author : jiangxinyu
 * @date : 2020/4/17
 */
@UtilityClass
public class EnvConverter {
    public static EnvGroupEntity dtoToEntity(EnvGroup envGroup) {
        if (envGroup == null) {
            return null;
        }
        return SimpleConverter.convert(envGroup, EnvGroupEntity.class);
    }

    public static EnvGroup entityToDto(EnvGroupEntity envGroupEntity) {
        if (envGroupEntity == null) {
            return null;
        }
        EnvGroup envGroup = SimpleConverter.convert(envGroupEntity, EnvGroup.class);
        Map<String, ClusterEnum> clusterInfo = envGroupEntity.getClusterInfo();

        //设置默认值
        if (clusterInfo == null && envGroupEntity.getData() != null) {
            clusterInfo = envGroupEntity.getData()
                    .stream()
                    .flatMap((Function<LinkedHashMap<String, String>, Stream<String>>) envData -> envData.keySet().stream())
                    .distinct()
                    .filter(key -> !"bizLine".equals(key))
                    .filter(key -> !"envVarDesc".equals(key))
                    .filter(key -> !"envVarName".equals(key))
                    .filter(key -> !"key".equals(key))
                    .collect(Collectors.toMap(key -> key, val -> ClusterEnum.OFFLINE));
            envGroup.setClusterInfo(clusterInfo);

        }
        return envGroup;
    }
}
