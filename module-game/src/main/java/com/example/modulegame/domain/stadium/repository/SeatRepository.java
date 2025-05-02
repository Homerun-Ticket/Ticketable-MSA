package com.example.modulegame.domain.stadium.repository;

import com.example.modulegame.domain.game.dto.SeatDetailDto;
import com.example.modulegame.domain.stadium.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface SeatRepository extends JpaRepository<Seat, Long> {

    boolean existsBySectionId(Long sectionId);

    @Query("select seat "
        + "   from Seat seat "
        + "   join fetch seat.section "
        + "   join fetch seat.section.stadium "
        + "  where seat.id in :ids ")
    List<Seat> findAllByIds(List<Long> ids);

    List<Seat> findBySectionId(Long sectionId);
    @Query(
        " select new com.example.modulegame.domain.game.dto.SeatDetailDto(" +
        " seat.id, " +
        " seat.position, " +
        " game.id, " +
        " game.startTime, " +
        " game.home, " +
        " game.away, " +
        " section.id, " +
        " section.type, " +
        " game.point, " +
        " section.extraCharge " +
        " ) " +
        "   from Game game " +
        "  inner join Stadium stadium " +
        "          on game.stadium = stadium " +
        "   left join Section section " +
        "          on section.stadium = stadium " +
        "         and section.id = :sectionId " +
        "   left join Seat seat " +
        "          on seat.section = section " +
        " where seat.id in :seatIds" +
        "   and game.id = :gameId "
    )
    List<SeatDetailDto> findSeatDtosByGameAndSection(
        Long gameId,
        Long sectionId,
        List<Long> seatIds
    );

    @Query(
        " select new com.example.modulegame.domain.game.dto.SeatDetailDto(" +
        " seat.id, " +
        " seat.position, " +
        " game.id, " +
        " game.startTime, " +
        " game.home, " +
        " game.away, " +
        " section.id, " +
        " section.type, " +
        " game.point, " +
        " section.extraCharge " +
        " ) " +
        "   from Game game " +
        "  inner join Stadium stadium " +
        "          on game.stadium = stadium " +
        "   left join Section section " +
        "          on section.stadium = stadium " +
        "   left join Seat seat " +
        "          on seat.section = section " +
        " where seat.id in :seatIds" +
        "   and game.id = :gameId "
    )
    List<SeatDetailDto> findSeatDtosByGame(Long gameId, List<Long> seatIds);

    @Query("SELECT s FROM Seat s JOIN FETCH s.section WHERE s.id = :seatId")
    Optional<Seat> findByIdWithSection(@Param("seatId") Long seatId);
}
