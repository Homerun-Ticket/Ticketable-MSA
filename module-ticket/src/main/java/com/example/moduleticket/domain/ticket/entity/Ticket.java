package com.example.moduleticket.domain.ticket.entity;

import com.example.moduleticket.domain.reservation.entity.Reservation;
import com.example.moduleticket.global.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Ticket extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long memberId;

	@Column(nullable = false)
	private Long gameId;

	@OneToOne
	@JoinColumn(name = "reservation_id", nullable = false)
	private Reservation reservation;

	private LocalDateTime deletedAt;

	@Builder
	public Ticket(Reservation reservation, Long memberId, Long gameId) {
		this.reservation = reservation;
		this.memberId = memberId;
		this.gameId = gameId;
	}

	public void cancel() {
		deletedAt = LocalDateTime.now();
	}
}
