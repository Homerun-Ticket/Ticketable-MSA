package com.example.moduleauction.domain.auction.service;

import static com.example.moduleauction.config.exception.ErrorCode.*;

import com.example.moduleauction.domain.auction.dto.AuctionDetailDto;
import com.example.moduleauction.domain.auction.dto.RecoveryDto;
import com.example.moduleauction.domain.auction.dto.RefundDto;
import com.example.moduleauction.domain.auction.event.BidSaveEvent;
import com.example.moduleauction.domain.auction.event.AuctionDetailSaveEvent;
import com.example.moduleauction.feign.GameClient;
import com.example.moduleauction.feign.SeatClient;
import com.example.moduleauction.feign.TicketClient;
import com.example.moduleauction.feign.dto.GameDto;
import com.example.moduleauction.feign.dto.SectionAndPositionDto;
import com.example.moduleauction.feign.dto.TicketDto;
import com.example.moduleauction.domain.auction.dto.TicketChangeOwnerDto;
import com.example.moduleauction.util.AuctionDetailRedisUtil;
import com.example.moduleauction.domain.auction.entity.AuthUser;
import com.example.moduleauction.config.exception.ServerException;
import com.example.moduleauction.domain.auction.event.BidUpdateEvent;
import com.example.moduleauction.domain.auction.dto.request.AuctionBidRequest;
import com.example.moduleauction.domain.auction.dto.request.AuctionCreateRequest;
import com.example.moduleauction.domain.auction.dto.request.AuctionSearchCondition;
import com.example.moduleauction.domain.auction.dto.response.AuctionBidResponse;
import com.example.moduleauction.domain.auction.dto.response.AuctionResponse;
import com.example.moduleauction.domain.auction.entity.Auction;
import com.example.moduleauction.domain.auction.entity.AuctionTicketInfo;
import com.example.moduleauction.domain.auction.repository.AuctionHistoryRepository;
import com.example.moduleauction.domain.auction.repository.AuctionRepository;
import com.example.moduleauction.util.AuctionBidRedisUtil;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuctionService {

	public static final int BID_UNIT = 100;
	private static final int CHUNK_SIZE = 500;

	private final AuctionRepository auctionRepository;
	private final AuctionHistoryRepository auctionHistoryRepository;

	private final AuctionTicketInfoService auctionTicketInfoService;
	private final AuctionHistoryService auctionHistoryService;
	private final AuctionPaymentService auctionPaymentService;
	private final PointQueueService pointQueueService;
	private final TicketQueueService ticketQueueService;
	private final AuctionBidRedisUtil auctionBidRedisUtil;
	private final AuctionDetailRedisUtil auctionDetailRedisUtil;
	private final ApplicationEventPublisher applicationEventPublisher;

	private final TicketClient ticketClient;
	private final GameClient gameClient;
	private final SeatClient seatClient;

	@Transactional
	public AuctionResponse createAuction(AuthUser authUser, AuctionCreateRequest dto) {
		TicketDto ticket = ticketClient.getTicket(authUser.getMemberId(), dto.getTicketId());
		GameDto game = gameClient.getGame(ticket.getGameId());
		SectionAndPositionDto sectionAndPositions = seatClient.getSectionAndPositions(ticket.getSeatIds());

		// 경기 시작 24시간 전 경매등록 검증
		if (game.isTimeOver()) {
			throw new ServerException(AUCTION_TIME_OVER);
		}

		// 경매 진행여부 검증
		if (auctionRepository.existsByTicketIdAndDeletedAtIsNull(ticket.getId())) {
			throw new ServerException(AUCTION_DUPLICATION);
		}

		// 되팔이 여부 검증
		if (auctionHistoryRepository.existsByTicketId(ticket.getId())) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		// 좌석 정렬
		List<String> sortedPositions = sectionAndPositions.getPositions().stream()
			.sorted(Comparator.comparingInt(pos -> Integer.parseInt(pos.split(" ")[1])))
			.toList();

		AuctionTicketInfo auctionTicketInfo = auctionTicketInfoService.createAuctionTicketInfo(ticket, game,
			sortedPositions);

		Auction auction = Auction.builder()
			.sellerId(authUser.getMemberId())
			.ticketId(ticket.getId())
			.startPoint(dto.getStartPoint())
			.bidPoint(dto.getStartPoint())
			.auctionTicketInfo(auctionTicketInfo)
			.build();

		Auction savedAuction = auctionRepository.save(auction);

		AuctionDetailDto auctionDetail = AuctionDetailDto.of(auction, auctionTicketInfo, ticket, game,
			sectionAndPositions, sortedPositions);

		applicationEventPublisher.publishEvent(new AuctionDetailSaveEvent(savedAuction.getId(), auctionDetail));
		applicationEventPublisher.publishEvent(new BidSaveEvent(savedAuction.getId(), savedAuction.getBidPoint()));

		return AuctionResponse.of(auctionDetail, savedAuction.getBidPoint());
	}

	@Transactional(readOnly = true)
	public AuctionResponse getAuction(Long auctionId) {
		AuctionDetailDto auctionDetail = auctionDetailRedisUtil.getAuctionDetail(auctionId);
		Integer bidPoint = auctionBidRedisUtil.getBidPoint(auctionId);
		return AuctionResponse.of(auctionDetail, bidPoint);
	}

	@Transactional(readOnly = true)
	public PagedModel<AuctionResponse> getAuctions(AuctionSearchCondition dto, Pageable pageable) {
		Page<Long> pages = auctionRepository.findByConditions(dto, pageable);
		List<Long> ids = pages.getContent();
		if (ids.isEmpty()) {
			Page<AuctionResponse> emptyPages = new PageImpl<>(Collections.emptyList(), pageable,
				pages.getTotalElements());
			return new PagedModel<>(emptyPages);
		}

		// 2) Redis에서 상세정보와 입찰가 일괄 조회
		Map<Long, AuctionDetailDto> detailMap = auctionDetailRedisUtil.getAuctionDetails(ids);
		Map<Long, Integer> bidMap = auctionBidRedisUtil.getBidPoints(ids);

		// 3) ID 순서 유지하며 AuctionResponse 리스트 생성
		List<AuctionResponse> responses = ids.stream()
			.map(id -> {
				AuctionDetailDto detail = detailMap.get(id);
				Integer bidPoint = bidMap.getOrDefault(id, detail.getStartPoint());
				return AuctionResponse.of(detail, bidPoint);
			})
			.collect(Collectors.toList());

		// 4) PageImpl으로 반환
		Page<AuctionResponse> auctionResponses = new PageImpl<>(responses, pageable, pages.getTotalElements());
		return new PagedModel<>(auctionResponses);
	}

	public AuctionBidResponse getBidPoint(Long auctionId) {
		Integer latestBidPoint = auctionBidRedisUtil.getBidPoint(auctionId);
		return new AuctionBidResponse(latestBidPoint);
	}

	@Transactional
	public AuctionResponse bidAuction(AuthUser authUser, Long auctionId, AuctionBidRequest dto) {

		// 0. Redis 사전 검증
		auctionBidRedisUtil.validateBid(auctionId, dto.getCurrentBidPoint());

		// 1. 경매 조회
		Auction auction = findAuction(auctionId);

		// 2. 입찰자가 눈으로 확인한 금액과, 실제 입찰가가 맞지 않는 경우 예외처리
		if (auction.isBidPointChanged(dto.getCurrentBidPoint())) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 3. 경매가 종료시간이 된 경우 예외처리
		if (auction.isTimeOver()) {
			expireAuction(List.of(auction));
			throw new ServerException(AUCTION_TIME_OVER);
		}

		// 4. 경매 등록자와 입찰자가 같은 경우 예외처리
		if (auction.isSameSellerAndBidder(authUser.getMemberId())) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		// 5. 시작가보다 낮은 금액 예외처리
		if (auction.isBidPointEnough(dto.getCurrentBidPoint())) {
			throw new ServerException(INVALID_BIDDING_AMOUNT);
		}

		// 6. 동일한 사람 연속입찰 예외처리
		if (auction.isSameBidder(authUser.getMemberId())) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		// 7. 입찰자 포인트 확인 및 회수
		Integer nextBid = auction.getBidPoint() + BID_UNIT;
		auctionPaymentService.processPayment(authUser.getMemberId(), nextBid, "BID");

		// 8~9. 해당 경매기록에서, 가격이 같은 기록이 존재하면 예외처리 + 경매기록 저장
		auctionHistoryService.createAuctionHistory(auction, authUser.getMemberId(), dto);

		// 10. 이전 입찰자에게 입찰금 환급
		if (auction.hasBidder()) {
			auctionPaymentService.processRefund(auction.getBidderId(), auction.getBidPoint(), "BID_REFUND");
		}

		// 11. 입찰내용 업데이트
		auction.updateBid(authUser.getMemberId(), nextBid);

		// 12. Event 발행을 통한 로직 실행순서 통제
		applicationEventPublisher.publishEvent(new BidUpdateEvent(auctionId, nextBid));

		AuctionDetailDto auctionDetail = auctionDetailRedisUtil.getAuctionDetail(auctionId);
		return AuctionResponse.of(auctionDetail, nextBid);
	}

	@Transactional
	public void deleteAuction(AuthUser authUser, Long auctionId) {
		Auction auction = findAuction(auctionId);

		if (auction.hasBidder()) {
			throw new ServerException(EXIST_BID);
		}

		if (auction.isNotOwner(authUser.getMemberId())) {
			throw new ServerException(AUCTION_ACCESS_DENIED);
		}

		expireAuction(List.of(auction));
	}

	private Auction findAuction(Long auctionId) {
		return auctionRepository.findByIdWithPessimisticLock(auctionId)
			.orElseThrow(() -> new ServerException(AUCTION_NOT_FOUND));
	}

	private void expireAuction(List<Auction> auctions) {
		if (auctions.isEmpty()) { return; }
		List<Long> auctionIds = auctions.stream().map(Auction::getId).toList();
		auctionRepository.softDeleteAllByIds(auctionIds);

		for (Auction auction : auctions) {
			auctionBidRedisUtil.deleteBidKey(auction.getId());
			auctionDetailRedisUtil.deleteAuctionDetail(auction.getId());
		}
	}

	/*
	 * 경기 취소 시 로직
	 * 최종 낙찰자에 대한 포인트 환불 + 판매자 포인트 회수
	 */
	@Transactional
	public void deleteAllAuctionsByCanceledGame(Long gameId) {
		List<Long> ticketIds = ticketClient.getTickets(gameId).stream().map(TicketDto::getId).toList();
		List<Auction> auctions = auctionRepository.findAllByTicketIdIn(ticketIds);

		List<RefundDto> refundList = auctions.stream()
			.filter(Auction::hasBidder)
			.map(auction -> new RefundDto(
				auction.getBidderId(),
				auction.getBidPoint().longValue()
			))
			.toList();

		List<RecoveryDto> recoveryList = auctions.stream()
			.filter(Auction::hasBidder)
			.filter(Auction::isExpired)
			.map(auction -> new RecoveryDto(
				auction.getSellerId(),
				auction.getBidPoint().longValue()
			))
			.toList();

		pointQueueService.enqueueRefundAuction(refundList);
		pointQueueService.enqueueRecoveryAuction(recoveryList);

		List<Auction> inProgressAuctions = auctions.stream().filter(Auction::isInProgress).toList();
		expireAuction(inProgressAuctions);
	}

	// 경매 종료 스케쥴러
	@Scheduled(fixedRate = 60000) // 1분마다 실행
	@Transactional
	public void closeExpiredAuctions() {
		Pageable pageable = PageRequest.of(0, CHUNK_SIZE);
		LocalDateTime standardTime = LocalDateTime.now().minusHours(24);
		Page<Auction> auctions = auctionRepository.findAllByDeletedAtIsNullAndCreatedAtBetween(
			standardTime.minusMinutes(60), standardTime, pageable
		);
		if (auctions.isEmpty()) { return; }

		List<RefundDto> refundList = auctions.stream()
			.filter(Auction::hasBidder)
			.map(auction -> new RefundDto(
				auction.getSellerId(),
				auction.getBidPoint().longValue()
			))
			.toList();
		pointQueueService.enqueueRefundAuction(refundList);

		List<TicketChangeOwnerDto> ticketChangeOwnerList = auctions.stream()
			.filter(Auction::hasBidder)
			.map(auction -> new TicketChangeOwnerDto(
				auction.getTicketId(),
				auction.getBidderId(),
				auction.getBidPoint().longValue()
			))
			.toList();
		ticketQueueService.enqueueTicketChangeOwner(ticketChangeOwnerList);

		expireAuction(auctions.getContent());
	}
}
