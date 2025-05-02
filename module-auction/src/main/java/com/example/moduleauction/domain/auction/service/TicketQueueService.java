package com.example.moduleauction.domain.auction.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.moduleauction.domain.auction.dto.TicketChangeOwnerDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TicketQueueService {

	private final RedisTemplate<String, Object> redisTemplate;

	private static final String STREAM_KEY = "ticket_change_owner_stream";

	public void enqueueTicketChangeOwner(List<TicketChangeOwnerDto> list) {
		redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
			for (TicketChangeOwnerDto dto : list) {
				Map<byte[], byte[]> body = new HashMap<>();
				body.put("ticketId".getBytes(), dto.getTicketId().toString().getBytes());
				body.put("newOwnerId".getBytes(), dto.getNewOwnerId().toString().getBytes());
				body.put("bidPoint".getBytes(), dto.getBidPoint().toString().getBytes());

				connection.streamCommands().xAdd("ticket_change_owner_stream".getBytes(), body);
			}
			return null;
		});
	}
}
