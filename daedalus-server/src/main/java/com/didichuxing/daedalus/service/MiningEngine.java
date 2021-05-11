package com.didichuxing.daedalus.service;

import com.didichuxing.daedalus.pojo.PipelineContext;
import com.didichuxing.daedalus.service.digger.Digger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : jiangxinyu
 * @date : 2020/4/21
 */
@Service
public class MiningEngine {
    @Autowired
    private List<Digger> diggers;

    public void dig(PipelineContext pipelineContext) {
        diggers.stream()
                .filter(digger -> digger.condition(pipelineContext))
                .forEach(digger -> digger.dig(pipelineContext));

    }
}
