package com.example.modulegame.domain.stadium.service;

import com.example.modulegame.global.exception.ErrorCode;
import com.example.modulegame.global.exception.ServerException;
import com.example.modulegame.domain.game.dto.SeatDetailDto;
import com.example.modulegame.domain.stadium.dto.SectionAndPositionDto;
import com.example.modulegame.domain.stadium.dto.request.SeatCreateRequest;
import com.example.modulegame.domain.stadium.dto.request.SeatUpdateRequest;
import com.example.modulegame.domain.stadium.dto.response.SeatCreateResponse;
import com.example.modulegame.domain.stadium.dto.response.SeatUpdateResponse;
import com.example.modulegame.domain.stadium.entity.Seat;
import com.example.modulegame.domain.stadium.entity.Section;
import com.example.modulegame.domain.stadium.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.example.modulegame.global.exception.ErrorCode.SEAT_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatRepository seatRepository;

    private final SectionService sectionService;

    @Transactional
    public List<SeatCreateResponse> createSeats(Long sectionId, SeatCreateRequest request) {
        Section section = sectionService.getById(sectionId);

        if (seatRepository.existsBySectionId(sectionId)){
            throw new ServerException(ErrorCode.SEATS_ALREADY_EXISTS);
        }

        List<List<String>> colNums = request.getColNums();
        List<List<Boolean>> isBlind = request.getIsBlind();;

        // 일관성 검사
        if (colNums.size() != isBlind.size()) {
            throw new ServerException(ErrorCode.COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE);
        }
        for (int i = 0; i < colNums.size(); i++) {
            if (colNums.get(i).size() != isBlind.get(i).size()) {
                throw new ServerException(ErrorCode.COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE);
            }
        }

        int sum = 0;
        List<SeatCreateResponse> seatList = new ArrayList<>();
        for (int i = 0; i < colNums.size(); i++) {
            for (int j = 0; j < colNums.get(i).size(); j++) {
                Seat seat = seatRepository.save(
                        Seat.builder()
                                .position(i+1+"열 "+ colNums.get(i).get(j))
                                .isBlind(isBlind.get(i).get(j))
                                .section(section)
                                .build()
                );
                sum++;
                seatList.add(SeatCreateResponse.of(seat));
            }
        }
        section.getStadium().updateCapacity(sum);
        return seatList;
    }

    @Transactional
    public SeatUpdateResponse updateSeat(Long seatId, SeatUpdateRequest request) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new ServerException(SEAT_NOT_FOUND));

        if (seat.isBlind() == request.isBlind()){
            throw new ServerException(ErrorCode.BLIND_STATUS_ALREADY_SET);
        }
        seat.updateBlind();

        return SeatUpdateResponse.of(seat);
    }

    @Transactional
    public void delete(Long seatId) {
        Seat seat = seatRepository.findById(seatId).orElseThrow(() -> new ServerException(SEAT_NOT_FOUND));

        seat.delete();
    }

    //경기에 포함된 좌석을 조회
	public List<SeatDetailDto> getSeatsByGameAndSection(Long gameId, Long sectionId, List<Long> seatIds ) {
        List<SeatDetailDto> seats = seatRepository.findSeatDtosByGameAndSection(gameId, sectionId, seatIds);
        if(seats.size() != seatIds.size()){
            throw new ServerException(SEAT_NOT_FOUND);
        }
        return seats;
    }

    public List<SeatDetailDto> getSeatsByGame(Long gameId, List<Long> seatIds) {
        List<SeatDetailDto> seats = seatRepository.findSeatDtosByGame(gameId, seatIds);
        if(seats.size() != seatIds.size()){
            throw new ServerException(SEAT_NOT_FOUND);
        }
        return seats;
    }
    public List<Seat> getSeatsBySectionId(Long sectionId) {
        return seatRepository.findBySectionId(sectionId);
    }
    public SectionAndPositionDto getSectionAndPositions(List<Long> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        return SectionAndPositionDto.from(seats);
    }

    public Seat getSeat(Long seatId) {
        return seatRepository.findByIdWithSection(seatId).orElseThrow(() -> new ServerException(SEAT_NOT_FOUND));
    }
}
