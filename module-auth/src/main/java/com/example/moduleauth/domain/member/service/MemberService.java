package com.example.moduleauth.domain.member.service;

import com.example.moduleauth.domain.member.dto.request.DeleteMemberRequest;
import com.example.moduleauth.domain.member.dto.request.UpdatePasswordRequest;
import com.example.moduleauth.domain.member.entity.Member;
import com.example.moduleauth.domain.member.repository.MemberRepository;
import com.example.moduleauth.global.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.moduleauth.global.exception.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class MemberService {
	
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	
	@Transactional
	public void updatePassword(Long memberId, UpdatePasswordRequest request) {
		if (request.getOldPassword().equals(request.getNewPassword())) {
			throw new ServerException(PASSWORD_SAME_AS_OLD);
		}
		
		Member member = getMember(memberId);
		matchPassword(request.getOldPassword(), member.getPassword());
		
		member.changePassword(passwordEncoder.encode(request.getNewPassword()));
	}
	
	@Transactional
	public void deleteMember(Long memberId, DeleteMemberRequest request) {
		Member member = getMember(memberId);
		matchPassword(request.getPassword(), member.getPassword());
		
		member.memberDelete();
	}
	
	/**
	 * 해당 멤버 아이디를 통해 멤버를 가져옴.
	 * 만약 해당 멤버가 존재하지 않거나, 삭제되었다면 예외를 던짐
	 */
	private Member getMember(Long memberId) {
		return memberRepository.findMemberById(memberId)
			.orElseThrow(() -> new ServerException(USER_NOT_FOUND));
	}
	
	/**
	 * 입력한 비밀번호와 해당 멤버의 비밀번호가 같은지 확인하는 메서드
	 */
	private void matchPassword(String inputPassword, String memberPassword) {
		if(!passwordEncoder.matches(inputPassword, memberPassword)) {
			throw new ServerException(INVALID_PASSWORD);
		}
	}
}
