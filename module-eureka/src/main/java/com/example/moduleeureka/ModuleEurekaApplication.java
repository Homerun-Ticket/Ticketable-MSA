package com.example.moduleeureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
//sdfsdffs
@SpringBootApplication
@EnableEurekaServer
public class ModuleEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleEurekaApplication.class, args);
    }

}
