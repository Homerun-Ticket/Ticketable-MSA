package com.example.moduleauth.common.role;

import java.util.Arrays;

import com.example.moduleauth.global.exception.ErrorCode;
import com.example.moduleauth.global.exception.ServerException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;



@Getter
@RequiredArgsConstructor
public enum MemberRole implements GrantedAuthority {
	
	ROLE_MEMBER(Authority.MEMBER),
	ROLE_ADMIN(Authority.ADMIN);
	
	private final String role;
	
	public static MemberRole of(String role) {
		return Arrays.stream(MemberRole.values())
			.filter(f -> f.name().equalsIgnoreCase(role))
			.findFirst()
			.orElseThrow(() -> new ServerException(ErrorCode.INVALID_USER_ROLE));
	}
	
	@Override
	public String getAuthority() {
		return name();
	}
	
	public static class Authority {
		public static final String MEMBER = "ROLE_MEMBER";
		public static final String ADMIN = "ROLE_ADMIN";
	}
}
