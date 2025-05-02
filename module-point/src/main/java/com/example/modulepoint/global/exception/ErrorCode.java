package com.example.modulepoint.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

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
	IMAGE_DELETE_FAILED("S3 이미지 삭제 실패", INTERNAL_SERVER_ERROR),

	// 티켓
	TICKET_NOT_FOUND("해당하는 티켓을 찾을 수 없습니다.", BAD_REQUEST),
	TICKET_PAYMENT_NOT_FOUND("티켓 결제 내역이 존재하지않습니다.", BAD_REQUEST),
	TICKET_ALREADY_RESERVED("이미 예매된 좌석입니다.", HttpStatus.CONFLICT),

	//예약
	RESERVATION_NOT_FOUND("예약 내역이 존재하지않습니다.", BAD_REQUEST),
	INVALID_RESERVATION_STATE("취소할 수 없는 예약입니다", BAD_REQUEST),

	//대기열
	INVALID_WAITING_TOKEN("올바르지않은 대기열 토큰 입니다.", BAD_REQUEST),
	NOT_ALLOW_CREATE_TOKEN("토큰을 생성할수 없는 요청입니다.", BAD_REQUEST),

	// 경매
	AUCTION_DUPLICATION("이미 경매에 등록한 티켓입니다.", CONFLICT),
	AUCTION_ACCESS_DENIED("경매에 대한 권한이 없습니다.", FORBIDDEN),
	AUCTION_TIME_OVER("경매 시간이 지났습니다.", BAD_REQUEST),
	AUCTION_NOT_FOUND("경매를 찾을 수 없습니다", NOT_FOUND),
	INVALID_BIDDING_AMOUNT("입찰액이 잘못되었습니다.", BAD_REQUEST),
	EXIST_BID("입찰이 진행중입니다.", BAD_REQUEST),

	// 포인트
	NOT_ENOUGH_POINT("포인트가 부족합니다.", BAD_REQUEST),
	EXCHANGE_WAITING("환전 대기 상태라 신청이 불가능합니다.", CONFLICT),
	POINT_EXCHANGE_NOT_FOUND("해당 포인트 내역이 없습니다.", NOT_FOUND),
	EXCHANGE_REQUEST_NOT_STATE("환전 요청 상태가 아닙니다.", BAD_REQUEST),
	PAYMENT_NOT_SUCCESS("아직 결제가 완료되지 않았습니다.", BAD_REQUEST),
	PAYMENT_VALID_ERROR("결제 검증중 오류가 발생했습니다.", INTERNAL_SERVER_ERROR),
	CAN_NOT_EXCHANGE("100원 미만은 환불이 불가능합니다.", BAD_REQUEST),
	ALREADY_EXCHANGE_STATE("이미 환전된 상태입니다.", BAD_REQUEST),
	ALREADY_PAYMENT_SUCCESS("이미 충전된 상태입니다.", BAD_REQUEST),
	ALREADY_PAYMENT_FAILED("결제에 실패했습니다.", CONFLICT),

	// 유저
	USER_EMAIL_DUPLICATION("다른 유저와 이메일이 중복됩니다.", CONFLICT),
	USER_NOT_LOGIN("로그인이 필요합니다. 로그인을 해주세요.", UNAUTHORIZED),
	USER_NOT_FOUND("해당하는 유저를 찾을 수 없습니다.", NOT_FOUND),
	INVALID_PASSWORD("패스워드가 올바르지 않습니다.", BAD_REQUEST),
	PASSWORD_SAME_AS_OLD("이전 패스워드와 동일할 수 없습니다.", BAD_REQUEST),
	USER_ACCESS_DENIED("사용자가 접근할 수 있는 권한이 없습니다.", FORBIDDEN),
	USER_ROLE_SAME_AS_OLD("이전 역활과 동일할 수 없습니다.", BAD_REQUEST),
	INVALID_USER_ROLE("유효하지 않는 role 입니다.", BAD_REQUEST),
	INVALID_RECAPTCHA_TOKEN("Recaptcha 토큰이 유효하지 않습니다.", BAD_REQUEST),
	INVALID_TOKEN("유효하지 않은 토큰입니다.", INTERNAL_SERVER_ERROR);

	private final String message;
	private final HttpStatus status;
}
