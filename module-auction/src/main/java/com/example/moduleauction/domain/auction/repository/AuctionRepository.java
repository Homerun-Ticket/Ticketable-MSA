package com.example.moduleauction.domain.auction.repository;

import com.example.moduleauction.domain.auction.entity.Auction;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AuctionRepository extends JpaRepository<Auction, Long>, AuctionRepositoryQuery {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query(" SELECT a " +
		"      FROM Auction a" +
		"     WHERE a.id = :id AND a.deletedAt is null")
	Optional<Auction> findByIdWithPessimisticLock(@Param("id") Long id);

	Page<Auction> findAllByDeletedAtIsNullAndCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore, Pageable pageable);

	boolean existsByTicketIdAndDeletedAtIsNull(Long ticketId);

	List<Auction> findAllByTicketIdIn(Collection<Long> ticketIds);

	@Modifying
	@Query("UPDATE Auction a "
		+ "    SET a.deletedAt = now()"
		+ "  WHERE a.id IN :ids")
	void softDeleteAllByIds(@Param("ids") List<Long> ids);
}
