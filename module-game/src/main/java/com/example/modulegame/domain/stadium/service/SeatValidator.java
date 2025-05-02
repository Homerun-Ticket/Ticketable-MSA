package com.example.modulegame.domain.stadium.service;


import java.util.List;

import com.example.modulegame.global.exception.ServerException;
import com.example.modulegame.domain.game.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.example.modulegame.global.exception.ErrorCode.SEAT_NOT_FOUND;


@Component
@RequiredArgsConstructor
public class SeatValidator {
	private final GameRepository gameRepository;
	//좌석이 경기에 포함되어있는지 판단
	public void validateSeatsBelongToGame(Long gameId, List<Long> seatIds) {
		List<Long> validSeatIds = gameRepository.findValidSeatIdsByGameId(gameId, seatIds);
		if (validSeatIds.size() != seatIds.size()) {
			throw new ServerException(SEAT_NOT_FOUND);
		}
	}
}
