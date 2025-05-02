package com.example.modulegame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
// 테스트 주석669988446633ess
@SpringBootApplication
@EnableDiscoveryClient
public class ModuleGameApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleGameApplication.class, args);
	}

}
