package com.beanSpot.WEB3_4_Poten_BE.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberAdminService {

	private final MemberRepository memberRepository;

	// 전체 회원 목록 조회
	public List<Member> getAllMembers() {
		log.info("전체 회원 목록 조회");
		return memberRepository.findAll();
	}

	// 회원 유형별 목록 조회
	public List<Member> getMembersByType(Member.MemberType memberType) {
		log.info("회원 유형별 목록 조회: {}", memberType);
		return memberRepository.findByMemberType(memberType);
	}

	// 회원 상세 정보 조회
	public Member getMemberById(Long memberId) {
		log.info("회원 상세 정보 조회: ID={}", memberId);
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new ServiceException(400, "회원을 찾을 수 없습니다."));
	}

}