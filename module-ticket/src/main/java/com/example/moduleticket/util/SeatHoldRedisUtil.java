package com.example.moduleticket.util;


import static com.example.moduleticket.global.exception.ErrorCode.PROCESSING_RESERVATION_SEAT;
import static com.example.moduleticket.global.exception.ErrorCode.SEAT_HOLD_EXPIRED;
import static com.example.moduleticket.global.exception.ErrorCode.TICKET_ALREADY_RESERVED;

import com.example.moduleticket.global.exception.ServerException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatHoldRedisUtil {

	private final RedisTemplate<String, String> redisTemplate;
	private final DefaultRedisScript<Long> holdSeatRedisScript;
	private final DefaultRedisScript<Long> releaseSeatRedisScript;
	private final DefaultRedisScript<Long> checkHeldRedisScript;

	private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(15);
	private static final String SEAT_HOLD_TTL_STRING = String.valueOf(SEAT_HOLD_TTL.getSeconds());

	public void holdSeatAtomic(Long gameId, List<Long> seatIds, String value) {
		List<String> keys = seatIds.stream().map(id -> createKey(id, gameId)).toList();
		Long execute = redisTemplate.execute(holdSeatRedisScript, keys, value, SEAT_HOLD_TTL_STRING);

		if(execute == null || execute.equals(0L)) {
			throw new ServerException(PROCESSING_RESERVATION_SEAT);
		}
	}

	public void releaseSeatAtomic(List<Long> seatIds, Long gameId) {
		List<String> keys = seatIds.stream().map(id -> createKey(id, gameId)).toList();
		redisTemplate.execute(releaseSeatRedisScript, keys);
	}

	public void checkHeldSeatAtomic(List<Long> seatIds, Long gameId, String value) {
		List<String> keys = new ArrayList<>(seatIds.size());
		for (Long seatId : seatIds) {
			keys.add(createKey(seatId, gameId));
		}

		Long isHeld = redisTemplate.execute(checkHeldRedisScript, keys, value);

		if (isHeld == null || isHeld.equals(0L)) {
			log.debug("다른 사람이 선점한 좌석입니다.");
			throw new ServerException(TICKET_ALREADY_RESERVED);
		}

		if(isHeld.equals(-1L)) {
			throw new ServerException(SEAT_HOLD_EXPIRED);
		}

	}

	public String createKey(Long seatId, Long gameId){
		return "game:" + gameId
			+ ":seat:" + seatId;
	}
}
