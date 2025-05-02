package com.example.moduleauction.util;

import static com.example.moduleauction.config.exception.ErrorCode.INVALID_BIDDING_AMOUNT;

import com.example.moduleauction.config.exception.ServerException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionBidRedisUtil {

	private final RedisTemplate<String, String> redisTemplate;
	private static final String PREFIX = "auction:bidPoint:";
	private static final Duration AUCTION_BID_TTL = Duration.ofHours(24);

	public void saveBidPoint(Long auctionId, Integer bidPoint) {
		String key = buildKey(auctionId);
		redisTemplate.opsForValue().set(key, String.valueOf(bidPoint), AUCTION_BID_TTL);
	}

	public Integer getBidPoint(Long auctionId) {
		String value = redisTemplate.opsForValue().get(buildKey(auctionId));
		if (value == null) return null;
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public Map<Long, Integer> getBidPoints(List<Long> auctionIds) {
		List<String> keys = auctionIds.stream()
			.map(id -> buildKey(id))
			.toList();

		List<String> values = redisTemplate.opsForValue().multiGet(keys);
		if (Objects.requireNonNull(values).isEmpty()) {
			return null;
		}

		Map<Long, Integer> result = new HashMap<>();
		for (int i = 0; i < auctionIds.size(); i++) {
			String value = values.get(i);
			result.put(auctionIds.get(i), Integer.parseInt(value));
		}
		return result;
	}

	public void validateBid(Long auctionId, Integer currentBid) {
		String key = buildKey(auctionId);
		String cachedBid = redisTemplate.opsForValue().get(key);
		if (cachedBid == null || !cachedBid.equals(String.valueOf(currentBid))) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}
	}

	public void updateBidKey(Long auctionId, Integer nextBid) {
		String key = buildKey(auctionId);
		redisTemplate.opsForValue().set(key, String.valueOf(nextBid));
	}

	public void deleteBidKey(Long auctionId) {
		String key = buildKey(auctionId);
		redisTemplate.delete(key);
	}

	private static String buildKey(Long auctionId) {
		return PREFIX + auctionId;
	}
}