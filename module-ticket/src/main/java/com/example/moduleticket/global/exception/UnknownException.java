package com.example.moduleticket.global.exception;

public class UnknownException extends RuntimeException {

	public UnknownException() {
		super("서비스가 불안정합니다.");
	}
}
