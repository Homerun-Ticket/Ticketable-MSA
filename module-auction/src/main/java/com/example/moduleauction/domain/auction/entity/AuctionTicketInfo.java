package com.example.moduleauction.domain.auction.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "auction_ticket_info", indexes = {
   @Index(name = "idx_isTogether_seatCount", columnList = "isTogether, seatCount")
})
public class AuctionTicketInfo {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

    private Integer seatCount;

    private Boolean isTogether;

    private LocalDateTime gameStartTime;

    private String home;

    private String away;

    @Builder
    public AuctionTicketInfo(Integer seatCount, Boolean isTogether, LocalDateTime gameStartTime, String home,
        String away) {
        this.seatCount = seatCount;
        this.isTogether = isTogether;
        this.gameStartTime = gameStartTime;
        this.home = home;
        this.away = away;
    }
}
