package com.example.moduleauth.domain.member.controller;

import com.example.moduleauth.domain.member.dto.request.DeleteMemberRequest;
import com.example.moduleauth.domain.member.dto.request.UpdatePasswordRequest;
import com.example.moduleauth.domain.member.dto.request.UpdatePasswordV2Request;
import com.example.moduleauth.domain.member.service.MemberService;
import com.example.moduleauth.global.annotation.LoginUser;
import com.example.moduleauth.global.entity.AuthUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class MemberController {
	
	private final MemberService memberService;
	
	@PatchMapping("/v1/members")
	public ResponseEntity<String> updatePasswordV1(
		@LoginUser AuthUser authUser,
		@Valid @RequestBody UpdatePasswordRequest request
	) {
		memberService.updatePasswordV1(authUser.getMemberId(), request);
		return ResponseEntity.ok("비밀번호가 변경되었습니다.");
	}
	
	@PatchMapping("/v2/members")
	public ResponseEntity<String> updatePasswordV2(
		@LoginUser AuthUser authUser,
		@Valid @RequestBody UpdatePasswordV2Request request
	) {
		memberService.updatePasswordV2(authUser.getMemberId(), request);
		return ResponseEntity.ok("비밀번호가 변경되었습니다.");
	}
	
	@DeleteMapping("/v1/members")
	public ResponseEntity<String> deleteMember(
		@LoginUser AuthUser authUser,
		@Valid @RequestBody DeleteMemberRequest request
	) {
		memberService.deleteMember(authUser.getMemberId(), request);
		return ResponseEntity.ok("멤버가 삭제되었습니다.");
	}
}
