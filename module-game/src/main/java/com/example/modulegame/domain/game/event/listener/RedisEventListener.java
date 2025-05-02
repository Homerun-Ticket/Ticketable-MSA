package com.example.modulegame.domain.game.event.listener;

import com.example.modulegame.domain.game.event.TicketEvent;
import com.example.modulegame.domain.game.service.GameCacheService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisEventListener implements MessageListener {
    private final ObjectMapper objectMapper;
    private final GameCacheService gameCacheService;

    public void onMessage(Message message, byte[] pattern) {
        try {
            String json = new String(message.getBody());
            TicketEvent event = objectMapper.readValue(json, TicketEvent.class);

            // 캐시 갱신 처리
            gameCacheService.handleAfterTicketChangeAll(event.getGameId(), event.getSeatId());

            log.info("✅ 티켓 변경 이벤트 수신 - gameId: {}", event.getGameId());

        } catch (Exception e) {
            log.error("❌ 이벤트 수신 중 에러 발생", e);
        }
    }
}
