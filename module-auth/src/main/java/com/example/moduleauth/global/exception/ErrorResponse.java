package com.example.moduleauth.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class ErrorResponse {
	private final String status;
	private final String message;
	private final String code;
	private final Map<String, String> errors;

	public ErrorResponse(String status, String message, String code) {
		this.status = status;
		this.message = message;
		this.code = code;
		this.errors = new HashMap<String, String>();
	}
}
