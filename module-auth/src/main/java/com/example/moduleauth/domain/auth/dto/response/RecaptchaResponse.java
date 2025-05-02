package com.example.moduleauth.domain.auth.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class RecaptchaResponse {
	
	private final boolean success;
}