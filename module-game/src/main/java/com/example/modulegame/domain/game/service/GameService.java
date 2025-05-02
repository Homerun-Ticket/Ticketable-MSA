package com.example.modulegame.domain.game.service;


import com.example.modulegame.common.image.ImageService;
import com.example.modulegame.domain.game.dto.GameDto;
import com.example.modulegame.domain.game.dto.request.GameCreateRequest;
import com.example.modulegame.domain.game.dto.request.GameUpdateRequest;
import com.example.modulegame.domain.game.dto.response.GameCreateResponse;
import com.example.modulegame.domain.game.dto.response.GameGetResponse;
import com.example.modulegame.domain.game.dto.response.GameUpdateResponse;
import com.example.modulegame.domain.game.entity.Game;
import com.example.modulegame.domain.game.repository.GameRepository;
import com.example.modulegame.domain.stadium.dto.response.SeatGetResponse;
import com.example.modulegame.domain.stadium.dto.response.SectionSeatCountResponse;
import com.example.modulegame.domain.stadium.dto.response.SectionTypeSeatCountResponse;
import com.example.modulegame.domain.stadium.dto.response.StadiumGetResponse;
import com.example.modulegame.domain.stadium.entity.Stadium;
import com.example.modulegame.domain.stadium.service.StadiumService;
import com.example.modulegame.feign.client.AuctionClient;
import com.example.modulegame.feign.client.TicketClient;
import com.example.modulegame.global.exception.ErrorCode;
import com.example.modulegame.global.exception.ServerException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final GameRepository gameRepository;
    private final StadiumService stadiumService;
    private final ImageService imageService;
    private final GameCacheService gameCacheService;
    private final TicketClient ticketClient;
    private final AuctionClient auctionClient;

    private static final String GAME_FOLDER = "game/";

    @Transactional
    public GameCreateResponse createGame(GameCreateRequest request, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileKey = GAME_FOLDER + UUID.randomUUID()+ "_" + originalFilename;
        String imagePath = imageService.saveFile(file, fileKey);
        if (request.getStartTime().minusDays(7).isBefore(LocalDateTime.now())){
            throw new ServerException(ErrorCode.INVALID_TICKETING_START_TIME);
        }
        try {
            Stadium stadium = stadiumService.getStadium(request.getStadiumId());
            Game game = gameRepository.save(Game.builder()
                    .stadium(stadium)
                    .away(request.getAway())
                    .home(request.getHome())
                    .type(request.getType())
                    .point(request.getPoint())
                    .imagePath(imagePath)
                    .ticketingStartTime((request.getStartTime().minusDays(7)))
                    .startTime(request.getStartTime())
                    .build()
            );
            return GameCreateResponse.of(game);
        } catch (ServerException e) {
            imageService.deleteFile(imagePath); // 이미지 삭제 로직
            throw new ServerException(ErrorCode.GAME_SAVE_FAILED);
        }
    }

    public List<GameGetResponse> getGames(String team, LocalDateTime date) {
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (date != null) {
            LocalDateTime[] range = getDayRange(date);
            start = range[0];
            end = range[1];
        }

        return gameCacheService.getGamesCached(team, start, end);
    }

    public StadiumGetResponse getSeatCountsByType(Long gameId) {
        Stadium stadium = gameRepository.getStadiumByGameId(gameId);
        List<SectionTypeSeatCountResponse> seatCountsByTypeCached = gameCacheService.getSeatCountsByTypeCached(gameId);

        return StadiumGetResponse.of(stadium, seatCountsByTypeCached);
    }

    public List<SectionSeatCountResponse> getSeatCountsBySection(Long gameId, String type) {
       return gameCacheService.getSeatCountsBySectionCached(gameId, type);
    }

    public List<SeatGetResponse> getSeats(Long sectionId, Long gameId) {
        return gameCacheService.getSeatsCached(sectionId, gameId);
    }

    @Transactional
    public GameUpdateResponse updateGame(Long gameId, GameUpdateRequest request) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));
        game.updateStartTime(request.getStartTime());
        return GameUpdateResponse.of(game);
    }

    @Transactional
    public void deleteGames(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));
        game.cancel();
        ticketClient.deleteAllTicketsByCanceledGame(gameId);
        auctionClient.deleteAllAuctionsByCanceledGame(gameId);
        gameCacheService.clearAllGameCaches();
    }

    // 날짜 계산 메서드
    private LocalDateTime[] getDayRange(LocalDateTime dateTime) {
        LocalDateTime startOfDay = dateTime.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return new LocalDateTime[] { startOfDay, endOfDay };
    }


	public GameDto getGameDto(Long gameId) {
        Game game = gameRepository.findById(gameId).orElseThrow(() -> new ServerException(ErrorCode.GAME_NOT_FOUND));

        return GameDto.from(game);
    }

    public List<GameDto> getGameDtoList(List<Long> gameIds) {
        List<Game> games = gameRepository.findAllById(gameIds);

        return games.stream().map(GameDto::from).toList();
    }
}
