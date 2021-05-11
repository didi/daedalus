package com.didichuxing.daedalus.pojo;

import com.didichuxing.daedalus.entity.step.BaseStepEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/3/25
 */
public class PipelineContext {

    private PipelineContext() {
    }

    @Getter
    private Map<String, String> vars = new HashMap<>();
    @Getter
    private Map<String, Object> objVars = new HashMap<>();

    private final Map<Class<?>, Object> map = new HashMap<>();
    private final Map<Class<?>, List<?>> listMap = new HashMap<>();


    @Setter
    @Getter
    private String pipelineId;


    @Getter
    @Setter
    private List<List<BaseStepEntity>> paths;




    public static PipelineContext get(String pipelineId) {
        PipelineContext pipelineContext = new PipelineContext();
        pipelineContext.setPipelineId(pipelineId);
        return pipelineContext;
    }


    public <T> void put(Class<T> clazz, T obj) {
        map.put(clazz, obj);
    }

    @SuppressWarnings("all")
    public <T> T get(Class<T> clazz) {
        return (T) map.get(clazz);
    }

    @SuppressWarnings("all")
    public <T> T getOrDefault(Class<T> clazz, T value) {
        T t = (T) map.get(clazz);
        return t == null ? value : t;
    }


    public <T> void putList(Class<T> clazz, List<T> list) {
        listMap.put(clazz, list);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(Class<T> clazz) {
        return (List<T>) listMap.get(clazz);
    }
}
