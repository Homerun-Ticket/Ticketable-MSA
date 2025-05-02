package com.example.moduleticket.domain.ticket.repository;

import com.example.moduleticket.domain.ticket.dto.RefundDto;
import com.example.moduleticket.domain.ticket.entity.Ticket;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

	@Query("SELECT t "
		+ "   FROM Ticket t "
		+ "  WHERE t.id = :id "
		+ "    AND t.deletedAt is null "
		+ "    AND t.memberId = :memberId")
	Optional<Ticket> findByIdAndDeletedAtIsNull(Long id, Long memberId);

	@Query("SELECT t "
		+ "   FROM Ticket t  "
		+ "  WHERE t.memberId = :memberId "
		+ "    AND t.deletedAt is null ")
	List<Ticket> findAllByMemberIdWithGame(Long memberId);

	@Query("select t "
		+ "   from Ticket t "
		+ "   join fetch t.reservation "
		+ "  where t.reservation.id = :reservationId "
		+ "    and t.deletedAt is null ")
	Optional<Ticket> findByReservationId(Long reservationId);

	@Modifying
	@Query("update Ticket t "
		+ "    set t.deletedAt = now()"
		+ " where t.gameId = :gameId ")
	void softDeleteAllByGameId(Long gameId);

	@Query("select new com.example.moduleticket.domain.ticket.dto.RefundDto(t.memberId, sum(tp.totalPoint)) "
		+ "   from Ticket t "
		+ "  inner join TicketPayment tp"
		+ "          on tp.ticket = t "
		+ "  where t.gameId = :gameId "
		+ "    and t.deletedAt is null "
		+ "  group by t.memberId "
	)
	List<RefundDto> findRefundDtoByGameId(Long gameId);

	@Query("SELECT t "
		+ "   FROM Ticket t "
		+ "  WHERE t.deletedAt is null "
		+ "    AND t.gameId = :gameId")
	List<Ticket> findByGameId(Long gameId);

	@Modifying
	@Query("UPDATE Ticket t SET t.memberId = :newOwnerId WHERE t.id = :ticketId")
	void changeOwner(@Param("ticketId") Long ticketId, @Param("newOwnerId") Long newOwnerId);

}
