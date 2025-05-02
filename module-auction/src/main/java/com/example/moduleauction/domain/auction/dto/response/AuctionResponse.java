package com.example.moduleauction.domain.auction.dto.response;

import com.example.moduleauction.domain.auction.dto.AuctionDetailDto;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuctionResponse {

	private final Long id;

	private final Integer startPoint;

	private final Integer bidPoint;

	private final Integer standardPoint;

	private final String sectionType;

	private final String sectionCode;

	private final String seatInfo;

	private final Integer seatCount;

	private final Boolean isTogether;

	private final LocalDateTime gameStartTime;

	private final String home;

	private final String away;

	private final String gameType;

	private final LocalDateTime createdAt;


	public static AuctionResponse of(AuctionDetailDto auctionDetailDto, Integer bidPoint) {
		return new AuctionResponse(
			auctionDetailDto.getId(),
			auctionDetailDto.getStartPoint(),
			bidPoint,
			auctionDetailDto.getStandardPoint(),
			auctionDetailDto.getSectionType(),
			auctionDetailDto.getSectionCode(),
			auctionDetailDto.getSeatInfo(),
			auctionDetailDto.getSeatCount(),
			auctionDetailDto.getIsTogether(),
			auctionDetailDto.getGameStartTime(),
			auctionDetailDto.getHome(),
			auctionDetailDto.getAway(),
			auctionDetailDto.getGameType(),
			auctionDetailDto.getCreatedAt()
		);
	}
}
