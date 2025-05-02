package com.example.moduleticket.config;

import com.example.moduleticket.global.exception.UnknownException;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import java.net.ConnectException;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CircuitBreakerCustomConfig {

	@Bean
	public CircuitBreakerRegistry circuitBreakerRegistry(CircuitBreakerConfig config) {
		return CircuitBreakerRegistry.of(config);
	}


	@Bean
	public CircuitBreakerConfig circuitBreakerConfig() {
		return CircuitBreakerConfig.custom()
			.failureRateThreshold(50)
			.waitDurationInOpenState(Duration.ofMillis(10000))
			.permittedNumberOfCallsInHalfOpenState(3)
			.slidingWindowType(SlidingWindowType.COUNT_BASED)
			.slidingWindowSize(5)
			.recordExceptions(UnknownException.class, FeignException.class)         // 실패로 기록되어 실패율이 증가하는 예외 목록
			.build();
	}

}
