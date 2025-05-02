package com.example.moduleauth.common.util;

import com.example.moduleauth.common.role.MemberRole;
import com.example.moduleauth.global.exception.ServerException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static com.example.moduleauth.global.exception.ErrorCode.INVALID_TOKEN;


@Component
public class JwtUtil {

	private static final String BEARER_PREFIX = "Bearer ";
	
	@Value("${jwt.access.token}")
	private Long accessTokenTime;
	
	@Value("${jwt.secret.key}")
	private String secretKey;
	private Key key;
	private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	
	@PostConstruct
	public void init() {
		byte[] bytes = Base64.getDecoder().decode(secretKey);
		key = Keys.hmacShaKeyFor(bytes);
	}

	public String createAccessToken(Long userId, String email, String name, MemberRole role) {
		Date date = new Date();
		
		return BEARER_PREFIX + Jwts.builder()
			.setSubject(userId.toString())
			.claim("email", email)
			.claim("name", name)
			.claim("role", role)
			.setExpiration(new Date(date.getTime() + accessTokenTime))
			.setIssuedAt(date)
			.signWith(key, signatureAlgorithm)
			.compact();
	}
	
	public String substringToken(String token) {
		if (StringUtils.hasText(token) && token.startsWith(BEARER_PREFIX)) {
			return token.substring(BEARER_PREFIX.length());
		}
		throw new ServerException(INVALID_TOKEN);
	}
	
	public Claims extractClaims(String token) {
		return Jwts.parserBuilder()
			.setSigningKey(key)
			.build()
			.parseClaimsJws(token)
			.getBody();
	}
}
