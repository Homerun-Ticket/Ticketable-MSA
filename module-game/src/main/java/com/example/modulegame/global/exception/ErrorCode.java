package com.example.modulegame.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 경기장
	STADIUM_NOT_FOUND("해당하는 경기장을 찾을 수 없습니다.", NOT_FOUND),
	STADIUM_NAME_DUPLICATION("다른 경기장과 이름이 중복됩니다.", BAD_REQUEST),
	SECTION_NOT_FOUND("해당하는 구역을 찾을 수 없습니다.", NOT_FOUND),
	SECTION_CODE_DUPLICATION("다른 구역과 코드가 중복됩니다.", BAD_REQUEST),
	SEAT_NOT_FOUND("해당하는 좌석을 찾을 수 없습니다.", NOT_FOUND),
	COLUMN_NUMS_AND_BLIND_STATUS_NOT_SAME_SIZE("열 번호와 시야 방해 여부 리스트의 크기는 같아야 합니다.", BAD_REQUEST),
	BLIND_STATUS_ALREADY_SET("시야 방해석 상태가 이미 요청된 상태와 동일합니다.", BAD_REQUEST),
	SEATS_ALREADY_EXISTS("이미 구역에 좌석이 있습니다.", BAD_REQUEST),
	SEAT_HOLD_EXPIRED("선점 좌석이 만료되었습니다", BAD_REQUEST),

	// 경기
	GAME_NOT_FOUND("해당하는 경기를 찾을 수 없습니다.", NOT_FOUND),
	GAME_SAVE_FAILED("경기 저장 실패", INTERNAL_SERVER_ERROR),
	INVALID_TICKETING_START_TIME("경기 시작 시간이 유효하지 않습니다. (최소 7일 이후)", BAD_REQUEST),
	ALREADY_CANCELED_GAME("이미 취소된 경기 입니다", BAD_REQUEST),

	// 이미지
	IMAGE_UPLOAD_FAILED("S3 이미지 업로드 실패", INTERNAL_SERVER_ERROR),
	IMAGE_DELETE_FAILED("S3 이미지 삭제 실패", INTERNAL_SERVER_ERROR);

	private final String message;
	private final HttpStatus status;
}
