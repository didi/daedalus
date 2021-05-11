package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.dal.PipelineDal;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author : jiangxinyu
 * @date : 2020/5/12
 */
@Slf4j
@Service
public class ConfigService {
    @Autowired
    private PipelineDal pipelineDal;

    private final LoadingCache<String, Map<String, Long>> CACHE = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.HOURS)
            .build(new CacheLoader<String, Map<String, Long>>() {
                @Override
                public Map<String, Long> load(String key) throws Exception {
                    if ("tag".equals(key)) {
                        return pipelineDal.queryTags();
                    }
                    return null;
                }
            });


    @SneakyThrows
    public List<String> tagSuggest(String key) {
        Map<String, Long> tagCounts = CACHE.get("tag");

        return tagCounts.keySet().stream()
                .sorted((o1, o2) -> (int) (tagCounts.get(o2) - tagCounts.get(o1)))
                .filter(tag -> {
                    if (StringUtils.isNotBlank(key)) {
                        return tag.contains(key);
                    }
                    return true;
                })
                .limit(5)
                .collect(Collectors.toList());
    }


}
