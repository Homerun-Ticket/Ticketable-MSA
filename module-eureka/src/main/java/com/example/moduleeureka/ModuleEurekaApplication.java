package com.example.moduleeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
// 테스트
public class ModuleEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleEurekaApplication.class, args);
    }

}
