package com.example.moduleauction.feign.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PointPaymentRequestDto {
	private final String idempotencyKey;
	private final String type;
	private final int amount;
	private final Long memberId;
}