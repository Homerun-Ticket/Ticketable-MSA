package com.example.modulegateway.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class ValidateTokenGatewayFilterFactory
	extends AbstractGatewayFilterFactory<ValidateTokenGatewayFilterFactory.Config> {
	
	// 필터 팩토리가 Config 인스턴스를 받기 위해 사용
	@Getter
	@Setter
	public static class Config { private String requiredRole; }
	
	private final WebClient webClient = WebClient.create("http://localhost:8083");
	
	public ValidateTokenGatewayFilterFactory() {
		super(Config.class);
	}
	
	@Override
	public GatewayFilter apply(Config config) {
		return (exchange, chain) -> {
			String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
			
			// 헤더가 없거나 Bearer 로 시작하지 않으면 401
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
				return exchange.getResponse().setComplete();
			}
			
			// 인증 서버로 토큰 검증 요청
			return webClient.post()
				.uri(uriBuilder -> uriBuilder
					.path("/api/v1/auth/validate")
					.queryParam("requiredRole", config.getRequiredRole())
					.build())
				.header(HttpHeaders.AUTHORIZATION, authHeader)
				.exchangeToMono(response -> {
					if (response.statusCode().isError()) {
						exchange.getResponse().setStatusCode(response.statusCode());
						return exchange.getResponse().setComplete();
					}
					
					HttpHeaders header = response.headers().asHttpHeaders();
					String memberId = header.getFirst("memberId");
					String role = header.getFirst("role");
					
					ServerHttpRequest request = exchange.getRequest().mutate()
						.header("memberId", memberId)
						.header("role", role)
						.build();
					
					return chain.filter(exchange.mutate()
						.request(request)
						.build());
				});
		};
	}
}
