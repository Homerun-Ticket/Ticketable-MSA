package com.example.modulewaiting;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class ModuleWaitingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModuleWaitingApplication.class, args);
	}

}
