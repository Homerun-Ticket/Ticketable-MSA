package com.example.moduleauction.domain.auction.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.moduleauction.domain.auction.entity.Auction;
import com.example.moduleauction.domain.auction.entity.AuctionTicketInfo;
import com.example.moduleauction.feign.dto.GameDto;
import com.example.moduleauction.feign.dto.SectionAndPositionDto;
import com.example.moduleauction.feign.dto.TicketDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuctionDetailDto {

	private Long id;

	private Integer startPoint;

	private Integer standardPoint;

	private String sectionType;

	private String sectionCode;

	private String seatInfo;

	private Integer seatCount;

	private Boolean isTogether;

	private LocalDateTime gameStartTime;

	private String home;

	private String away;

	private String gameType;

	private LocalDateTime createdAt;

	public static AuctionDetailDto of(Auction auction, AuctionTicketInfo auctionTicketInfo, TicketDto ticketDto,
		GameDto gameDto, SectionAndPositionDto sectionAndPositionDto, List<String> sortedPositions) {
		return new AuctionDetailDto(
			auction.getId(),
			auction.getStartPoint(),
			ticketDto.getTotalPoint(),
			sectionAndPositionDto.getType(),
			sectionAndPositionDto.getCode(),
			String.join(", ", sortedPositions),
			auctionTicketInfo.getSeatCount(),
			auctionTicketInfo.getIsTogether(),
			auctionTicketInfo.getGameStartTime(),
			auctionTicketInfo.getHome(),
			auctionTicketInfo.getAway(),
			gameDto.getType(),
			auction.getCreatedAt()
		);
	}
}
