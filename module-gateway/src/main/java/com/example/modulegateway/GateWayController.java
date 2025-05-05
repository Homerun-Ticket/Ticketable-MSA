package com.example.modulegateway;

import com.example.modulegateway.dto.RouteCreateRequest;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GateWayController {
	private final RouteDefinitionWriter routeDefinitionWriter;
	private final RouteDefinitionLocator routeDefinitionLocator;
	private final ApplicationEventPublisher publisher;
	private final GateWayQueueService gateWayQueueService;

	@GetMapping("/health")
	public String health() {
		return "OK";
	}

	@PostMapping("/admin/routes")
	public Mono<Void> addRoute(@RequestBody RouteCreateRequest routeCreateRequest) {
		return routeDefinitionLocator.getRouteDefinitions()
			.filter(route -> route.getId().equals(routeCreateRequest.getApplyModule()))
			.next()
			.flatMap(route -> {
				RouteDefinition routeDefinition = new RouteDefinition();
				routeDefinition.setId(routeCreateRequest.getId());
				routeDefinition.setUri(URI.create(route.getUri().toString()));
				routeDefinition.setOrder(-1);

				// Predicate 생성 (Path 매칭)
				PredicateDefinition pathPredicate = new PredicateDefinition();
				pathPredicate.setName("Path");
				pathPredicate.setArgs(new HashMap<>(Map.of(
					"pattern", routeCreateRequest.getPathPattern()
				)));
				routeDefinition.setPredicates(List.of(pathPredicate));

				// Filters 생성
				FilterDefinition waitingQueueFilter = new FilterDefinition();
				waitingQueueFilter.setName("WaitingQueue");
				waitingQueueFilter.setArgs(new HashMap<>());

				FilterDefinition rewritePathFilter = new FilterDefinition();
				rewritePathFilter.setName("RewritePath");
				rewritePathFilter.setArgs(new HashMap<>(Map.of(
					"regexp", "/" + routeCreateRequest.getApplyModule() + "/(?<segment>.*)",
					"replacement", "/${segment}"
				)));

				routeDefinition.setFilters(List.of(waitingQueueFilter, rewritePathFilter));

				return routeDefinitionWriter.save(Mono.just(routeDefinition)).doOnSuccess(
					a -> publisher.publishEvent(new RefreshRoutesEvent(this))
				);
			});
	}

	@DeleteMapping("/admin/routes/{id}")
	public Mono<Void> deleteRoute(@PathVariable String id) {
		return routeDefinitionWriter.delete(Mono.just(id))
			.doOnSuccess(unused -> publisher.publishEvent(new RefreshRoutesEvent(this)));
	}

	@GetMapping("/admin/routes")
	public Flux<RouteDefinition> getRoutes() {
		return routeDefinitionLocator.getRouteDefinitions();
	}

	@PostMapping("/admin/waiting-trigger")
	public Mono<Void> check(@RequestBody(required = false) Map<String, Object> body){
		log.info("대기열 트리거 요청 수신! payload");
		String status = (String) body.get("status");
		if(status.equals("firing")){
			log.info("대기열 적용 요청 수신!");
			return gateWayQueueService.applyQueue();
		}
		if(status.equals("resolved")){
			log.info("대기열 해제 요청 수신!");
			return gateWayQueueService.deactivateQueue();
		}
		return Mono.empty();
	}
}
