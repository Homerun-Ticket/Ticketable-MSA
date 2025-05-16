package com.example.moduleticket.domain.reservation.service;


import static com.example.moduleticket.global.exception.ErrorCode.SEAT_HOLD_EXPIRED;
import static com.example.moduleticket.global.exception.ErrorCode.TICKET_ALREADY_RESERVED;

import com.example.moduleticket.domain.reservation.entity.Reservation;
import com.example.moduleticket.domain.reservation.repository.ReservationRepository;
import com.example.moduleticket.domain.ticket.repository.TicketSeatRepository;
import com.example.moduleticket.global.argumentresolver.AuthUser;
import com.example.moduleticket.global.exception.ServerException;
import com.example.moduleticket.util.SeatHoldRedisUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationValidator {
	private final ReservationRepository reservationRepository;
	private final TicketSeatRepository ticketSeatRepository;
	private final SeatHoldRedisUtil seatHoldRedisUtil;

	public void validateReservationForComplete(AuthUser authUser, Long gameId, List<Long> seatIds, Reservation reservation) {
		try {
			checkTicketSeatDuplicate(gameId, seatIds);
			seatHoldRedisUtil.checkHeldSeatAtomic(seatIds, gameId, String.valueOf(authUser.getMemberId()));
		} catch (ServerException e) {
			if (e.getErrorCode() == SEAT_HOLD_EXPIRED) {
				log.info("TTL 만료로 예약 만료 처리됨: reservationId = {}", reservation.getId());
				reservation.expiredPayment();
			}
			throw e;
		}

	}

	public void checkTicketSeatDuplicate(Long gameId, List<Long> seatIds) {
		if(ticketSeatRepository.existsByGameIdAndSeatIdInAndTicketDeletedAtIsNull(gameId, seatIds)) {
			log.debug("이미 예매된 좌석입니다.");
			throw new ServerException(TICKET_ALREADY_RESERVED);
		}
	}

	public void checkDuplicateReservation(List<Long> seatIds, Long gameId) {
		if(reservationRepository.existsByReserveSeats_SeatIdInAndStateAndGameId(
			seatIds,
			"WAITING_PAYMENT",
			gameId)
		) {
			throw new ServerException(TICKET_ALREADY_RESERVED);
		}
	}

}
