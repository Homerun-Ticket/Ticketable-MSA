package com.example.moduleauth.domain.member.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class UpdatePasswordV2Request {
	
	@NotNull(message = "이전 비밀번호를 입력해주세요.")
	private String oldPassword;
	
	@NotNull(message = "새로운 비밀번호를 입력해주세요.")
	@Pattern(
		message = "비밀번호는 8~12자의 영문, 숫자, 특수문자를 포함해야 합니다.",
		regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$"
	)
	private String newPassword;
	
	@NotNull
	private String recaptchaToken;
}
