package com.example.modulegame.domain.game.repository;


import com.example.modulegame.domain.game.entity.Game;
import com.example.modulegame.domain.stadium.dto.response.SectionCodeMapping;
import com.example.modulegame.domain.stadium.dto.response.SectionTypeMapping;
import com.example.modulegame.domain.stadium.entity.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long>, GameRepositoryQuery {

    @Query("SELECT g.stadium From Game g where g.id = :gameId")
    Stadium getStadiumByGameId(@Param("gameId") Long gameId);

    @Query(value = """
            SELECT s.type AS type, seat.id AS id
            FROM seat seat
            JOIN section s ON seat.section_id = s.id
            JOIN stadium st ON s.stadium_id = st.id
            JOIN game g ON g.stadium_id = st.id
            WHERE g.id = :gameId
            """, nativeQuery = true)
    List<SectionTypeMapping> findSeatsId(@Param("gameId") Long gameId);

    @Query(value = """
            SELECT
                s.code AS code, seat.id AS id
            FROM seat seat
            JOIN section s ON seat.section_id = s.id
            JOIN stadium st ON s.stadium_id = st.id
            JOIN game g ON g.stadium_id = st.id
            WHERE g.id = :gameId
             AND s.type = :type
            """, nativeQuery = true)
    List<SectionCodeMapping> findSeatsIdBySectionType(
            @Param("gameId") Long gameId,
            @Param("type") String type
    );

    @Query("select game.id "
            + "   from Game game"
            + "  inner join Stadium stadium "
            + "          on game.stadium = stadium "
            + "  inner join Section section"
            + "          on section.stadium = stadium "
            + "  inner join Seat seat"
            + "          on seat.section = section "
            + "  where game.id = :gameId"
            + "    and seat.id in :seatIds"
            + "    and game.deletedAt is null ")
    List<Long> findValidSeatIdsByGameId(Long gameId, List<Long> seatIds);
}