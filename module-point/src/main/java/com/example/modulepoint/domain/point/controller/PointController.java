package com.example.modulepoint.domain.point.controller;

import com.example.modulepoint.domain.point.dto.request.ChargePointRequest;
import com.example.modulepoint.global.annotation.LoginUser;
import com.example.modulepoint.global.entity.AuthUser;
import com.example.modulepoint.domain.point.dto.request.ExchangePointRequest;
import com.example.modulepoint.domain.point.dto.request.PointPaymentRequestDto;
import com.example.modulepoint.domain.point.dto.response.PointResponse;
import com.example.modulepoint.domain.point.enums.PointHistoryType;
import com.example.modulepoint.domain.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PointController {

	private final PointService pointService;

	@PostMapping("/v1/points/exchange")
	public ResponseEntity<PointResponse> exchangePoint(
		@LoginUser AuthUser authUser,
		@Valid @RequestBody ExchangePointRequest request
	) {
		return ResponseEntity.ok(pointService.exchangePoint(authUser.getMemberId(), request));
	}

	@GetMapping("/v1/points")
	public ResponseEntity<PointResponse> getMemberPoint(
		@LoginUser AuthUser authUser
		) {
		return ResponseEntity.ok(pointService.getMemberPoint(authUser.getMemberId()));
	}
	
	@PostMapping("/v1/points")
	public ResponseEntity<Void> createPoint(
		@RequestParam Long memberId
	) {
		pointService.createPoint(memberId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/internal/members/{memberId}/points/increment")
	public ResponseEntity<Void> increasePoint(
		@PathVariable Long memberId,
		@RequestBody PointPaymentRequestDto pointPaymentRequestDto
	) {
		PointHistoryType type = PointHistoryType.valueOf(pointPaymentRequestDto.getType());
		pointService.increasePoint(
			memberId,
			pointPaymentRequestDto.getAmount(),
			type
		);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/internal/members/{memberId}/points/decrement")
	public ResponseEntity<Void> decreasePoint(
		@PathVariable Long memberId,
		@RequestBody PointPaymentRequestDto pointPaymentRequestDto
	) {
		PointHistoryType type = PointHistoryType.valueOf(pointPaymentRequestDto.getType());
		pointService.decreasePoint(
			memberId,
			pointPaymentRequestDto.getAmount(),
			type
		);
		return ResponseEntity.ok().build();
	}

	// 어드민 포인트 충전 (테스트를 위함)
	@PostMapping("/v1/admin/points/charge")
	public ResponseEntity<PointResponse> chargePoint(
		@LoginUser AuthUser authUser,
		@RequestBody ChargePointRequest request
	) {
		return ResponseEntity.ok(pointService.chargePoint(authUser.getMemberId(), request));
	}
}
