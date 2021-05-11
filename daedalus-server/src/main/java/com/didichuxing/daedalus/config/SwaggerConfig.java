package com.didichuxing.daedalus.config;

import com.didichuxing.daedalus.common.Response;
import com.didichuxing.daedalus.common.dto.step.BaseStep;
import com.fasterxml.classmate.TypeResolver;
import com.google.common.collect.ImmutableMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author : jiangxinyu
 * @date : 2020/3/20
 */
@Configuration
public class SwaggerConfig {


    @Bean
    public Docket api() {
        Class[] classes = {BaseStep.class, Response.class};
        TypeResolver typeResolver = new TypeResolver();
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .alternateTypeRules(AlternateTypeRules.newRule(
                        typeResolver.resolve(Response.class, ImmutableMap.class),
                        typeResolver.resolve(List.class, Map.class), Ordered.HIGHEST_PRECEDENCE))
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.didichuxing.daedalus"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
//                .ignoredParameterTypes(classes);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo("Daedalus API", "Daedalus API", "1.0.0", null, null, null, null, Collections.EMPTY_LIST);
    }
}
