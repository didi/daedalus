package com.didichuxing.daedalus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableMongoAuditing
@EnableAspectJAutoProxy
@EnableAsync
@EnableCaching
@EnableScheduling
public class DaedalusApplication {

    public static void main(String[] args) {
        System.setProperty("nashorn.args", "--language=es6");
        SpringApplication.run(DaedalusApplication.class, args);
    }

}
