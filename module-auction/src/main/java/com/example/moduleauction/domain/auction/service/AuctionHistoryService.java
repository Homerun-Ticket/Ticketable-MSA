package com.example.moduleauction.domain.auction.service;


import static com.example.moduleauction.config.exception.ErrorCode.INVALID_BIDDING_AMOUNT;
import static com.example.moduleauction.domain.auction.service.AuctionService.BID_UNIT;

import com.example.moduleauction.config.exception.ServerException;
import com.example.moduleauction.domain.auction.dto.request.AuctionBidRequest;
import com.example.moduleauction.domain.auction.entity.Auction;
import com.example.moduleauction.domain.auction.entity.AuctionHistory;
import com.example.moduleauction.domain.auction.repository.AuctionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionHistoryService {

	private final AuctionHistoryRepository auctionHistoryRepository;

	@Transactional
	public void createAuctionHistory(Auction auction, Long bidderId, AuctionBidRequest dto) {
		// 6. 해당 경매기록에서, 가격이 같은 기록이 존재하면 예외처리
		if (auctionHistoryRepository.existsByAuctionAndPoint(auction, dto.getCurrentBidPoint() + BID_UNIT)) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 7. 경매기록 저장
		AuctionHistory auctionHistory = AuctionHistory.builder()
			.auction(auction)
			.bidderId(bidderId)
			.point(auction.getBidPoint() + BID_UNIT)
			.build();
		auctionHistoryRepository.save(auctionHistory);
	}
}
