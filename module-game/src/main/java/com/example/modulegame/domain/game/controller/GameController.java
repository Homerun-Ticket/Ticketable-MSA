package com.example.modulegame.domain.game.controller;


import com.example.modulegame.domain.game.dto.GameDto;
import com.example.modulegame.domain.game.dto.request.GameCreateRequest;
import com.example.modulegame.domain.game.dto.request.GameUpdateRequest;
import com.example.modulegame.domain.game.dto.response.GameCreateResponse;
import com.example.modulegame.domain.game.dto.response.GameGetResponse;
import com.example.modulegame.domain.game.dto.response.GameUpdateResponse;
import com.example.modulegame.domain.game.service.GameService;
import com.example.modulegame.domain.stadium.dto.response.SeatGetResponse;
import com.example.modulegame.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.modulegame.domain.stadium.dto.response.StadiumGetResponse;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;

    @PostMapping("/v1/admin/games")
    public ResponseEntity<GameCreateResponse> createGame(
            @Valid @RequestPart(value = "json") GameCreateRequest request,
            @RequestPart(value = "image") MultipartFile file
    ) {
        return ResponseEntity.ok(gameService.createGame(request, file));
    }

    @GetMapping("/v3/games")
    public ResponseEntity<List<GameGetResponse>> getGames(
            @RequestParam (required = false) String team,
            @RequestParam (required = false) LocalDateTime date
    ) {
        return ResponseEntity.ok(gameService.getGames(team, date));
    }

    @GetMapping("/v2/games/{gameId}")
    public ResponseEntity<StadiumGetResponse> getStadiumAndSectionSeatCountsV2(
            @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getSeatCountsByType(gameId));
    }

    @GetMapping("/v2/games/{gameId}/sectionTypes")
    public ResponseEntity<List<SectionSeatCountResponse>> getAvailableSeatsBySectionTypeV2(
            @PathVariable Long gameId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(gameService.getSeatCountsBySection(gameId, type));
    }

    @GetMapping("/v2/games/{gameId}/sections/{sectionId}")
    public ResponseEntity<List<SeatGetResponse>> getSeatInfoBySectionV2(
            @PathVariable Long gameId,
            @PathVariable Long sectionId
    ) {
        return ResponseEntity.ok(gameService.getSeats(sectionId, gameId));
    }

    @PutMapping("/v1/admin/games/{gameId}")
    public ResponseEntity<GameUpdateResponse> updateGame(
            @PathVariable Long gameId,
            @RequestBody GameUpdateRequest request
    ) {
        return ResponseEntity.ok(gameService.updateGame(gameId, request));
    }

    @DeleteMapping("/v1/admin/games/{gameId}")
    public ResponseEntity<Void> deleteGame(
            @PathVariable Long gameId
    ) {
        gameService.deleteGames(gameId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internal/games/{gameId}")
    public ResponseEntity<GameDto> getGame(
        @PathVariable Long gameId
    ) {
        return ResponseEntity.ok(gameService.getGameDto(gameId));
    }

    @GetMapping("/internal/games")
    public ResponseEntity<List<GameDto>> getGame(
        @RequestParam List<Long> gameIds
    ) {
        return ResponseEntity.ok(gameService.getGameDtoList(gameIds));
    }
}
