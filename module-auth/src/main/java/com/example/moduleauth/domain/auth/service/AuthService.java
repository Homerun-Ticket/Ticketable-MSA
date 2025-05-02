package com.example.moduleauth.domain.auth.service;

import com.example.moduleauth.domain.auth.dto.request.LoginRequest;
import com.example.moduleauth.domain.auth.dto.request.SignupRequest;
import com.example.moduleauth.domain.auth.dto.response.AuthResponse;
import com.example.moduleauth.feign.PointService;
import com.example.moduleauth.common.role.MemberRole;
import com.example.moduleauth.common.util.JwtUtil;
import com.example.moduleauth.domain.member.entity.Member;
import com.example.moduleauth.domain.member.repository.MemberRepository;
import com.example.moduleauth.global.exception.ServerException;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.moduleauth.global.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class AuthService {
	
	private final ReCaptchaService reCaptchaService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final PointService pointService;
	
	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (!request.validRePassword()) {
			throw new ServerException(INVALID_PASSWORD);
		}
		
		if (memberRepository.existsByEmail(request.getEmail())) {
			throw new ServerException(USER_EMAIL_DUPLICATION);
		}
		
		MemberRole memberRole = MemberRole.of(request.getRole());
		
		Member member = Member.builder()
			.email(request.getEmail())
			.name(request.getName())
			.password(passwordEncoder.encode(request.getPassword()))
			.role(request.getRole())
			.build();
		Member savedMember = memberRepository.save(member);

		pointService.createPoint(savedMember.getId());

		String accessToken = jwtUtil.createAccessToken(
			savedMember.getId(), savedMember.getEmail(), savedMember.getName(), memberRole
		);
		return new AuthResponse(accessToken);
	}
	
	@Transactional
	public AuthResponse login(LoginRequest request) {
		Member findMember = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
		
		if (!passwordEncoder.matches(request.getPassword(), findMember.getPassword())) {
			throw new ServerException(INVALID_PASSWORD);
		}
		
		MemberRole memberRole = MemberRole.of(findMember.getRole());
		
		String accessToken = jwtUtil.createAccessToken(
			findMember.getId(), findMember.getEmail(), findMember.getName(), memberRole
		);
		return new AuthResponse(accessToken);
	}

	@Transactional
	public AuthResponse loginV2(LoginRequest request) {
		if (!reCaptchaService.isValid(request.getRecaptchaToken())) {
			throw new ServerException(INVALID_RECAPTCHA_TOKEN);
		}
		
		Member findMember = memberRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
		
		if (!passwordEncoder.matches(request.getPassword(), findMember.getPassword())) {
			throw new ServerException(INVALID_PASSWORD);
		}
		
		MemberRole memberRole = MemberRole.of(findMember.getRole());
		
		String accessToken = jwtUtil.createAccessToken(
			findMember.getId(), findMember.getEmail(), findMember.getName(), memberRole
		);
		return new AuthResponse(accessToken);
	}
	
	@Transactional
	public Claims validateToken(String authToken, String requiredRole) {
		String token = jwtUtil.substringToken(authToken);
		
		Claims claims = jwtUtil.extractClaims(token);
		
		Long memberId = Long.valueOf(claims.getSubject());
		String email = claims.get("email", String.class);
		
		if (!memberRepository.existsByIdAndEmail(memberId, email)) {
			throw new ServerException(USER_NOT_FOUND);
		}
		String role = claims.get("role", String.class);
		MemberRole.of(role);
		
		if (requiredRole != null && !requiredRole.isEmpty() && !requiredRole.equals(role)) {
			throw new ServerException(USER_ACCESS_DENIED);
		}
		return claims;
	}
}
