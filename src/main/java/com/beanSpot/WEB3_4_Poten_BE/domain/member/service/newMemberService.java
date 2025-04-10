package com.beanSpot.WEB3_4_Poten_BE.domain.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req.UpdateMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class newMemberService {
	private final MemberRepository memberRepository;

	// 이메일로 회원 조회 (Optional 제거)
	public Member findByEmail(String email) {
		return memberRepository.findByEmail(email)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));
	}

	// 회원 정보 수정 또는 신규 가입
	@Transactional
	public Member modifyOrJoin(String oAuthId, String email, String name, String profileImg, Member.SnsType snsType) {
		return memberRepository.findByoAuthId(oAuthId)
			.map(member -> updateMemberInfo(member, email, name, profileImg, snsType)) // 기존 회원 업데이트
			.orElseGet(() -> createNewMember(oAuthId, email, name, profileImg, snsType)); // 신규 회원 생성
	}

	// 회원 정보 업데이트 (중복 제거)
	private Member updateMemberInfo(Member member, String email, String name, String profileImg, Member.SnsType snsType) {
		member.setName(name);
		member.setEmail(email);
		member.setProfileImg(profileImg);
		member.setSnsType(snsType);
		return memberRepository.save(member);
	}

	// 신규 회원 생성 (중복 제거)
	private Member createNewMember(String oAuthId, String email, String name, String profileImg, Member.SnsType snsType) {
		Member newMember = Member.builder()
			.oAuthId(oAuthId)
			.email(email)
			.name(name)
			.profileImg(profileImg)
			.snsType(snsType)
			.memberType(Member.MemberType.USER)
			.build();
		return memberRepository.save(newMember);
	}

	// 회원 정보 업데이트 (중복 제거 및 Optional 반환 제거)
	@Transactional
	public Member updateMemberInfo(String oAuthId, UpdateMemberMyPageDto dto, String currentEmail) {
		Member member = memberRepository.findByoAuthId(oAuthId)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));

		if (dto.email() != null && !dto.email().equals(currentEmail)) {
			validateEmailDuplication(dto.email(), oAuthId);
			member.setEmail(dto.email());
		}

		if (dto.name() != null) {
			member.setName(dto.name());
		}

		if (dto.phoneNumber() != null) {
			member.setPhoneNumber(dto.phoneNumber());
		}

		return memberRepository.save(member);
	}

	// 이메일 중복 확인 로직 분리
	private void validateEmailDuplication(String email, String oAuthId) {
		memberRepository.findByEmail(email).ifPresent(existingMember -> {
			if (!existingMember.getOAuthId().equals(oAuthId)) {
				throw new ServiceException("이미 사용 중인 이메일입니다.");
			}
		});
	}

	public Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));
	}

	public Optional<Member> findByOAuthId(String oAuthId) {
		return memberRepository.findByoAuthId(oAuthId);
	}
}