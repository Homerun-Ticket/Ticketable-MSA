package com.example.moduleticket.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class RedisStreamConfig {

	private static final String OWNER_STREAM = "ticket_change_owner_stream";
	private static final String OWNER_GROUP  = "ticket_change_owner_group";

	@Bean
	public ApplicationRunner initRedisStreamGroup(StringRedisTemplate redisTemplate) {
		return args -> {
			try {
				redisTemplate.opsForStream()
					.createGroup(OWNER_STREAM, ReadOffset.from("0"), OWNER_GROUP);
			} catch (Exception ignore) {
				// 이미 그룹이 있으면 무시
			}
		};
	}
}