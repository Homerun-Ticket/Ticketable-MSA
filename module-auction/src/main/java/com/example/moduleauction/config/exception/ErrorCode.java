package com.example.moduleauction.config.exception;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

	// 경매
	AUCTION_DUPLICATION("이미 경매에 등록한 티켓입니다.", CONFLICT),
	AUCTION_ACCESS_DENIED("경매에 대한 권한이 없습니다.", FORBIDDEN),
	AUCTION_TIME_OVER("경매 시간이 지났습니다.", BAD_REQUEST),
	AUCTION_NOT_FOUND("경매를 찾을 수 없습니다", NOT_FOUND),
	INVALID_BIDDING_AMOUNT("입찰액이 잘못되었습니다.", BAD_REQUEST),
	EXIST_BID("입찰이 진행중입니다.", BAD_REQUEST);

	private final String message;
	private final HttpStatus status;
}
