package com.didichuxing.daedalus.common.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.VirtualBeanPropertyWriter;
import com.fasterxml.jackson.databind.util.Annotations;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author : jiangxinyu
 * @date : 2021/1/8
 */
public class VirtualIdWriter extends VirtualBeanPropertyWriter {
    public VirtualIdWriter(BeanPropertyDefinition propDef, Annotations contextAnnotations, JavaType declaredType) {
        super(propDef, contextAnnotations, declaredType);
    }

    public VirtualIdWriter() {
    }

    @Override
    protected Object value(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception {
        return ThreadLocalRandom.current().nextLong(1000000000000000000L, Long.MAX_VALUE);
    }

    @Override
    public VirtualBeanPropertyWriter withConfig(MapperConfig<?> config, AnnotatedClass declaringClass, BeanPropertyDefinition propDef, JavaType type) {
        return new VirtualIdWriter(propDef, null, type);
    }


}
