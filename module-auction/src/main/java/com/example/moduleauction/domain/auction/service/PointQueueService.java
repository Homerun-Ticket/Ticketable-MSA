package com.example.moduleauction.domain.auction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.moduleauction.domain.auction.dto.RecoveryDto;
import com.example.moduleauction.domain.auction.dto.RefundDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PointQueueService {
	private final RedisTemplate<String, Object> refundObjectRedisTemplate;

	public void enqueueRefundAuction(List<RefundDto> refundDto) {
		refundObjectRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (RefundDto dto : refundDto) {
				Map<byte[], byte[]> body = new HashMap<>();
				body.put("memberId".getBytes(), dto.getMemberId().toString().getBytes());
				body.put("price".getBytes(), String.valueOf(dto.getPrice()).getBytes());

				// XADD: stream 이름, 데이터 map
				connection.streamCommands().xAdd(
					"refund_stream".getBytes(),
					body
				);
			}
			return null;
		});
	}

	public void enqueueRecoveryAuction(List<RecoveryDto> recoveryDto) {
		refundObjectRedisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (RecoveryDto dto : recoveryDto) {
				Map<byte[], byte[]> body = new HashMap<>();
				body.put("memberId".getBytes(), dto.getMemberId().toString().getBytes());
				body.put("price".getBytes(), String.valueOf(dto.getPrice()).getBytes());

				// XADD: stream 이름, 데이터 map
				connection.streamCommands().xAdd(
					"recovery_stream".getBytes(),
					body
				);
			}
			return null;
		});
	}
}
