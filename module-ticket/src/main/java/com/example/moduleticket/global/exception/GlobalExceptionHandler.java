package com.example.moduleticket.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(ServerException.class)
	public ResponseEntity<ErrorResponse> responseStatusExceptionException(ServerException e) {
		log.error(e.getMessage(), e);
		ErrorCode errorCode = e.getErrorCode();
		String status = errorCode.getStatus().toString();
		String message = e.getMessage();
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

	@ExceptionHandler(CallNotPermittedException.class)
	public ResponseEntity<ErrorResponse> callNotPermittedException(CallNotPermittedException ex) {
		HttpStatus status = INTERNAL_SERVER_ERROR;
		String message = ErrorCode.UNKNOWN_ERROR.getMessage();
		ErrorResponse response = new ErrorResponse(
			status.name(),
			message,
			String.valueOf(status.value()),
			null );
		return ResponseEntity.status(status).body(response);
	}
}
