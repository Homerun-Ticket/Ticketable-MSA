package com.example.modulegame.domain.stadium.controller;


import com.example.modulegame.domain.game.dto.SeatDetailDto;
import com.example.modulegame.domain.stadium.dto.SectionAndPositionDto;
import com.example.modulegame.domain.stadium.dto.request.SeatCreateRequest;
import com.example.modulegame.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.modulegame.domain.stadium.dto.response.SeatCreateResponse;
import com.example.modulegame.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.modulegame.domain.stadium.service.SeatService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class SeatController {
    private final SeatService seatService;

    @PostMapping("/v1/admin/sections/{sectionId}/seats")
    public ResponseEntity<List<SeatCreateResponse>> createSeats(
            @PathVariable Long sectionId,
            @RequestBody SeatCreateRequest request
    ) {
        return ResponseEntity.ok(seatService.createSeats(sectionId, request));
    }

    @PutMapping("/v1/admin/seats/{seatId}")
    public ResponseEntity<SeatUpdateResponse> updateSeat(
            @PathVariable Long seatId,
            @RequestBody SeatUpdateRequest request
    ) {
        return ResponseEntity.ok(seatService.updateSeat(seatId, request));
    }

    @DeleteMapping("/v1/admin/seats/{seatId}")
    public ResponseEntity<Void> deleteSeat(
            @PathVariable Long seatId
    ) {
        seatService.delete(seatId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/internal/seats/by-section")
    public ResponseEntity<List<SeatDetailDto>> getSeatsByGameAndSection(
        @RequestParam Long gameId,
        @RequestParam Long sectionId,
        @RequestParam List<Long> seatIds
    ) {
        return ResponseEntity.ok(seatService.getSeatsByGameAndSection(gameId, sectionId, seatIds));
    }

    @GetMapping("/internal/seats/by-game")
    public ResponseEntity<List<SeatDetailDto>> getSeatsByGame(
        @RequestParam List<Long> seatIds,
        @RequestParam Long gameId
    ) {
        return ResponseEntity.ok(seatService.getSeatsByGame(gameId, seatIds));
    }

    @PostMapping("/internal/sections/seats")
    public ResponseEntity<SectionAndPositionDto> getSectionAndPositions(
        @RequestBody List<Long> seatIds
    ) {
        return ResponseEntity.ok(seatService.getSectionAndPositions(seatIds));
    }
}
