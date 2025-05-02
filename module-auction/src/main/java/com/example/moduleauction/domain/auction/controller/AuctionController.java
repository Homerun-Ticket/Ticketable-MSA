package com.example.moduleauction.domain.auction.controller;

import static com.example.moduleauction.util.PageUtil.correctPageIndex;

import com.example.moduleauction.domain.auction.dto.request.AuctionBidRequest;
import com.example.moduleauction.domain.auction.dto.request.AuctionCreateRequest;
import com.example.moduleauction.domain.auction.dto.request.AuctionSearchCondition;
import com.example.moduleauction.domain.auction.dto.response.AuctionBidResponse;
import com.example.moduleauction.domain.auction.dto.response.AuctionResponse;
import com.example.moduleauction.domain.auction.service.AuctionService;
import com.example.moduleauction.config.annotation.LoginUser;
import com.example.moduleauction.domain.auction.entity.AuthUser;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuctionController {

	private final AuctionService auctionService;

	@PostMapping("/v1/auctions")
	public ResponseEntity<AuctionResponse> createAuction(
		@LoginUser AuthUser authUser,
		@Valid @RequestBody AuctionCreateRequest dto
	) {
		return ResponseEntity.ok(auctionService.createAuction(authUser, dto));
	}

	@GetMapping("/v1/auctions/{auctionId}")
	public ResponseEntity<AuctionResponse> getAuction(@PathVariable Long auctionId) {
		return ResponseEntity.ok(auctionService.getAuction(auctionId));
	}

	@GetMapping("/v1/auctions")
	public ResponseEntity<PagedModel<AuctionResponse>> getAuctions(
		@ModelAttribute AuctionSearchCondition dto,
		@PageableDefault(page = 1, size = 10) Pageable pageRequest
	) {
		return ResponseEntity.ok(auctionService.getAuctions(dto, correctPageIndex(pageRequest)));
	}

	@GetMapping(value = "/v1/auctions/{auctionId}/bid-point")
	public ResponseEntity<AuctionBidResponse> getBidPoint(
		@PathVariable Long auctionId
	) {
		return ResponseEntity.ok(auctionService.getBidPoint(auctionId));
	}

	@PostMapping("/v1/auctions/{auctionId}")
	public ResponseEntity<AuctionResponse> bidAuction(
		@LoginUser AuthUser authUser,
		@PathVariable Long auctionId,
		@Valid @RequestBody AuctionBidRequest dto
	) {
		return ResponseEntity.ok(auctionService.bidAuction(authUser, auctionId, dto));
	}

	@DeleteMapping("/v1/auctions/{auctionId}")
	public ResponseEntity<Void> deleteAuction(
		@LoginUser AuthUser authUser,
		@PathVariable Long auctionId
	) {
		auctionService.deleteAuction(authUser, auctionId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/auctions/game/{gameId}")
	void deleteAllAuctionsByCanceledGame(
		@PathVariable("gameId") Long gameId
	) {
		auctionService.deleteAllAuctionsByCanceledGame(gameId);
	};

}
