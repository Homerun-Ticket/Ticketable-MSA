package com.example.moduleticket.domain.reservation.repository;

import com.example.moduleticket.domain.reservation.entity.Reservation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@Query("select r "
		+ "   from Reservation r "
		+ "   join fetch r.reserveSeats "
		+ "  where r.id = :reservationId "
		+ "    and r.memberId = :memberId ")
	Optional<Reservation> findByIdMemberId(Long reservationId , Long memberId);

	@Query("SELECT rs.seatId " +
			"FROM Reservation r " +
			"JOIN ReserveSeat rs " +
			"ON r.id = rs.reservation.id " +
			"LEFT JOIN Ticket t " +
			"ON t.reservation = r " +
			"WHERE r.gameId = :gameId " +
			"AND (r.state = 'WAITING_PAYMENT'" +
			"OR (r.state = 'COMPLETE_PAYMENT' AND t.deletedAt IS NULL)) "
	)
	Set<Long> findBookedSeatIdByGameId(Long gameId);

	@Query("SELECT r " +
			"FROM Reservation r " +
//			"JOIN ReserveSeat rs " +
//			"ON r.id = rs.reservation.id " +
			"WHERE r.state = 'WAITING_PAYMENT' " +
			"AND r.createdAt < :expiredLimit"
	)
	List<Reservation> findExpiredReservations(LocalDateTime expiredLimit);

	boolean existsByReserveSeats_SeatIdInAndStateAndGameId(List<Long> reserveSeatIds, String state, Long gameId);
}
