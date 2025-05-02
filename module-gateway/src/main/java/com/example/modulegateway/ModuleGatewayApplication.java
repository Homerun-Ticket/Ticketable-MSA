package com.example.modulegateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ModuleGatewayApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(ModuleGatewayApplication.class, args);
	}
	
}
