package com.example.modulepoint.domain.exchange.controller;

import com.example.modulepoint.domain.exchange.dto.response.ExchangeHistoryResponse;
import com.example.modulepoint.domain.exchange.dto.response.ExchangeResponse;
import com.example.modulepoint.domain.exchange.service.ExchangeHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ExchangeHistoryController {

	private final ExchangeHistoryService exchangeHistoryService;

	@PatchMapping("/v1/admin/exchangeHistories/{exchangeHistoryId}/exchange")
	public ResponseEntity<ExchangeResponse> exchangePoint(
		@PathVariable Long exchangeHistoryId
	) {
		return ResponseEntity.ok(exchangeHistoryService.exchangePoint(exchangeHistoryId));
	}

	@GetMapping("/v1/admin/exchangeHistories/{exchangeHistoryId}")
	public ResponseEntity<ExchangeHistoryResponse> getExchangeHistory(
		@PathVariable Long exchangeHistoryId
	) {
		return ResponseEntity.ok(exchangeHistoryService.getExchangeHistory(exchangeHistoryId));
	}

	@GetMapping("/v1/admin/exchangeHistories")
	public ResponseEntity<PagedModel<ExchangeHistoryResponse>> getExchangeHistories(
		@RequestParam int page
	) {
		return ResponseEntity.ok(exchangeHistoryService.getExchangeHistories(page));
	}
}
