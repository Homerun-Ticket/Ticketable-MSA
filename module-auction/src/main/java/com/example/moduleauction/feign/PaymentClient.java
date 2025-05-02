package com.example.moduleauction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.moduleauction.feign.dto.PaymentDto;
import com.example.moduleauction.feign.dto.request.PointPaymentRequestDto;

@FeignClient(name = "payment", url = "http://localhost:8086/api")
public interface PaymentClient {

	@PostMapping("/internal/members/{memberId}/points/decrement")
	PaymentDto processPayment(
		@PathVariable Long memberId,
		@RequestBody PointPaymentRequestDto pointPaymentRequestDto
	);

	@PostMapping("/internal/members/{memberId}/points/increment")
	PaymentDto processRefund(
		@PathVariable Long memberId,
		@RequestBody PointPaymentRequestDto pointPaymentRequestDto
	);
}
