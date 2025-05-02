package com.example.moduleauth.domain.member.entity;

import com.example.moduleauth.global.entity.Timestamped;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class Member extends Timestamped {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(length = 50, unique = true)
	private String email;
	private String password;
	@Column(length = 20)
	private String name;
	
	@Column(length = 15)
	private String role;
	
	private LocalDateTime deletedAt;
	
	@Builder
	public Member(String email, String password, String name, String role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = role;
	}
	
	public void memberDelete() {
		this.deletedAt = LocalDateTime.now();
	}
	
	public void changePassword(String password) {
		this.password = password;
	}
}
