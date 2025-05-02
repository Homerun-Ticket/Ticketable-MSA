package com.example.modulepoint.domain.point.service;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.modulepoint.domain.point.dto.RecoveryDto;
import com.example.modulepoint.domain.point.dto.RefundDto;
import com.example.modulepoint.domain.point.enums.PointHistoryType;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecoveryStreamConsumer {

	private final RedisTemplate<String, Object> redisTemplate;
	private final PointService pointService;

	private static final String STREAM_KEY = "recovery_stream";
	private static final String GROUP_NAME = "recovery_group";
	private static final String CONSUMER_NAME = "recovery-consumer-1";

	@Async
	@EventListener(ApplicationReadyEvent.class)
	public void runWorker() {
		while (true) {
			try {
				List<MapRecord<String, Object, Object>> messages = redisTemplate.opsForStream().read(
					Consumer.from(GROUP_NAME, CONSUMER_NAME),
					StreamReadOptions.empty().count(10).block(Duration.ofSeconds(30)),
					StreamOffset.create(STREAM_KEY, ReadOffset.lastConsumed())
				);

				if (messages != null) {
					for (MapRecord<String, Object, Object> message : messages) {
						RecoveryDto dto = new ObjectMapper().convertValue(message.getValue(), RecoveryDto.class);
						try {
							pointService.decreasePoint(dto.getMemberId(), dto.getPrice(), PointHistoryType.RECOVERY);
							redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP_NAME, message.getId());
							log.info("회수 완료: {}", dto.getMemberId());
						} catch (Exception e) {
							log.error("회수 실패: {}", dto.getMemberId(), e);
						}
					}
				}
			} catch (Exception e) {
				handleGroupCreationError(e);
			}
		}
	}

	private void handleGroupCreationError(Exception e) {
		String root = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
		if (root.contains("NOGROUP")) {
			log.warn("그룹 없음 → 생성 시도");
			try {
				redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.from("0"), GROUP_NAME);
				log.info("그룹 생성 완료");
			} catch (Exception ex) {
				log.error("그룹 생성 실패: {}", ex.getMessage());
			}
		}
	}
}

