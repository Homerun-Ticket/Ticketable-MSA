package com.example.moduleauction.domain.auction.service;

import java.util.List;

import com.example.moduleauction.domain.auction.entity.AuctionTicketInfo;
import com.example.moduleauction.domain.auction.repository.AuctionTicketInfoRepository;
import com.example.moduleauction.feign.dto.GameDto;
import com.example.moduleauction.feign.dto.TicketDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionTicketInfoService {

	private final AuctionTicketInfoRepository auctionTicketInfoRepository;

	@Transactional
	public AuctionTicketInfo createAuctionTicketInfo(TicketDto ticket, GameDto game, List<String> sortedPositions) {
		// 연석 여부 검증
		Boolean isTogether = false;
		if (sortedPositions.size() > 1) {
			String prevRow = null;
			Integer prevColumn = null;
			isTogether = true; // 일단 연석이라고 가정하고, 조건을 깨면 false로 전환

			for (int i = 0; i < sortedPositions.size(); i++) {
				String[] parts = sortedPositions.get(i).split("열 ");
				String currentRow = parts[0];
				int currentColumn = Integer.parseInt(parts[1].replaceAll("\\D", ""));

				if (i == 0) {
					prevRow = currentRow;
					prevColumn = currentColumn;
					continue;
				}

				// 행이 다르거나 열이 연속되지 않으면 연석 아님
				if (!prevRow.equals(currentRow) || currentColumn != prevColumn + 1) {
					isTogether = false;
					break;
				}

				prevColumn = currentColumn;
			}
		}

		AuctionTicketInfo auctionTicketInfo = AuctionTicketInfo.builder()
			.seatCount(sortedPositions.size())
			.isTogether(isTogether)
			.gameStartTime(game.getStartTime())
			.home(game.getHome())
			.away(game.getAway())
			.build();

		return auctionTicketInfoRepository.save(auctionTicketInfo);
	}
}
