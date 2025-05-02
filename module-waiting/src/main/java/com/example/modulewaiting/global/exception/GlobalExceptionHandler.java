package com.example.modulewaiting.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponse> responseStatusExceptionException(ServerException e) {
		ErrorCode errorCode = e.getErrorCode();
		String status = errorCode.getStatus().toString();
		String message = errorCode.getMessage();
		String code = errorCode.name();
		ErrorResponse response = new ErrorResponse(status, message, code);
		return ResponseEntity.status(errorCode.getStatus()).body(response);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
		HttpStatus status = BAD_REQUEST;
		Map<String, String> fieldErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			fieldErrors.put(error.getField(), error.getDefaultMessage())
		);

		ErrorResponse response = new ErrorResponse(status.name(),
			"잘못된 요청입니다.",
			String.valueOf(status.value()),
			fieldErrors );

		return ResponseEntity.status(BAD_REQUEST).body(response);
	}
}
