package com.example.modulegateway.config;

import io.micrometer.common.KeyValue;
import io.micrometer.common.KeyValues;
import org.springframework.cloud.gateway.config.GatewayProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;
import org.springframework.http.server.reactive.observation.ServerRequestObservationConvention;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

@Configuration
public class GatewayUriMetricsConfiguration {

	private static final String KEY_URI = "uri";
	private static final String UNKNOWN = "UNKNOWN";

	@Bean
	public ServerRequestObservationConvention gatewayUriObservationConvention(GatewayProperties gatewayProperties) {
		// Gateway Route에서 Path Predicate 패턴 목록 미리 수집
		List<PathPattern> pathPatterns = gatewayProperties.getRoutes().stream()
			.flatMap(route -> route.getPredicates().stream()
				.filter(predicate -> "Path".equals(predicate.getName()))
				.flatMap(predicate -> predicate.getArgs().values().stream()))
			.map(pattern -> PathPatternParser.defaultInstance.parse(pattern))
			.toList();

		return new DefaultServerRequestObservationConvention() {
			@Override
			public KeyValues getLowCardinalityKeyValues(ServerRequestObservationContext context) {
				KeyValues originalKeyValues = super.getLowCardinalityKeyValues(context);

				if (isUriUnknown(originalKeyValues)) {
					String path = context.getCarrier().getPath().value();
					// 수집된 PathPattern과 매칭 시도
					for (PathPattern pattern : pathPatterns) {
						if (pattern.matches(PathContainer.parsePath(path))) {
							return originalKeyValues.and(KeyValue.of(KEY_URI, pattern.getPatternString()));
						}
					}
				}
				// 매칭 실패하면 원래대로 ("UNKNOWN")
				return originalKeyValues;
			}

			private boolean isUriUnknown(KeyValues keyValues) {
				return keyValues.stream()
					.filter(kv -> KEY_URI.equals(kv.getKey()))
					.findFirst()
					.map(kv -> UNKNOWN.equals(kv.getValue()))
					.orElse(true);
			}
		};
	}
}

