package com.example.modulewaiting.global.exception;


import static org.springframework.http.HttpStatus.BAD_REQUEST;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


	//대기열
	INVALID_WAITING_TOKEN("올바르지않은 대기열 토큰 입니다.", BAD_REQUEST),
	NOT_ALLOW_CREATE_TOKEN("토큰을 생성할수 없는 요청입니다.", BAD_REQUEST);

	private final String message;
	private final HttpStatus status;
}
