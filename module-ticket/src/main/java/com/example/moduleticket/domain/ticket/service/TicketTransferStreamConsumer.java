package com.example.moduleticket.domain.ticket.service;

import java.time.Duration;
import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.connection.stream.StreamReadOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.example.moduleticket.domain.ticket.dto.TicketChangeOwnerDto;
import com.example.moduleticket.domain.ticket.repository.TicketRepository;
import com.example.moduleticket.feign.PaymentClient;
import com.example.moduleticket.feign.dto.request.PointPaymentRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TicketTransferStreamConsumer {

	private final RedisTemplate<String, Object> redisTemplate;
	private final TicketRepository ticketRepository; // 직접 접근
	private final PaymentClient paymentClient;

	private static final String STREAM_KEY = "ticket_change_owner_stream";
	private static final String GROUP_NAME = "ticket_change_owner_group";
	private static final String CONSUMER_NAME = "ticket-change_owner-consumer-1";

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
						TicketChangeOwnerDto dto = new ObjectMapper().convertValue(message.getValue(), TicketChangeOwnerDto.class);
						try {
							ticketRepository.changeOwner(dto.getTicketId(), dto.getNewOwnerId());
							redisTemplate.opsForStream().acknowledge(STREAM_KEY, GROUP_NAME, message.getId());
							log.info("티켓 소유권 이전 완료: ticketId={}, to={}", dto.getTicketId(), dto.getNewOwnerId());
						} catch (Exception e) {
							log.error("티켓 이전 실패 → 포인트 환불 예정: {}", dto, e);
							PointPaymentRequestDto pointPaymentRequestDto = new PointPaymentRequestDto(
								dto.getNewOwnerId() + "REFUND","REFUND", Math.toIntExact(dto.getBidPoint()), dto.getNewOwnerId());
							paymentClient.processRefund(dto.getNewOwnerId(), pointPaymentRequestDto);
						}
					}
				}
			} catch (Exception e) {
				if (e.getMessage().contains("NOGROUP")) {
					redisTemplate.opsForStream().createGroup(STREAM_KEY, ReadOffset.from("0"), GROUP_NAME);
				} else {
					throw e;
				}
			}
		}
	}
}
