package com.example.moduleauction.domain.auction.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "auction", indexes = {
	@Index(name = "idx_deleted_at", columnList = "deleted_at"),
	@Index(name = "idx_deleted_at_created_at", columnList = "deleted_at, created_at")
})
public class Auction extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer startPoint;

	private Integer bidPoint;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "auction_ticket_info_id", nullable = false)
	private AuctionTicketInfo auctionTicketInfo;

	@Column(nullable = false)
	private Long ticketId;

	@Column(nullable = false)
	private Long sellerId;

	private Long bidderId;

	private LocalDateTime deletedAt;

	@Builder
	public Auction(Integer startPoint, Integer bidPoint, AuctionTicketInfo auctionTicketInfo, Long ticketId,
		Long sellerId, Long bidderId) {
		this.startPoint = startPoint;
		this.bidPoint = bidPoint;
		this.auctionTicketInfo = auctionTicketInfo;
		this.ticketId = ticketId;
		this.sellerId = sellerId;
		this.bidderId = bidderId;
	}

	public void softDelete() {
		if (this.deletedAt == null) {
			this.deletedAt = LocalDateTime.now();
		}
	}

	public void updateBid(Long bidderId, Integer bidPoint) {
		this.bidderId = bidderId;
		this.bidPoint = bidPoint;
	}

	public boolean isBidPointChanged(Integer currentBidPoint) {
		return !this.bidPoint.equals(currentBidPoint);
	}

	public boolean isTimeOver() {
		return this.getCreatedAt().plusHours(24).isBefore(LocalDateTime.now()) && this.deletedAt == null;
	}

	public boolean isSameSellerAndBidder(Long bidderId) {
		return this.sellerId.equals(bidderId);
	}

	public boolean hasBidder() {
		return this.bidderId != null;
	}

	public boolean isNotOwner(Long requestMemberId) {
		return !this.sellerId.equals(requestMemberId);
	}

	public boolean isBidPointEnough(Integer bidPoint) {
		return this.bidPoint > bidPoint;
	}

	public boolean isSameBidder(Long bidderId) {
		if (this.bidderId == null) {
			return false;
		}
		return this.bidderId.equals(bidderId);
	}

	public boolean isExpired() { return this.deletedAt != null; }

	public boolean isInProgress() { return this.deletedAt == null; }
}
