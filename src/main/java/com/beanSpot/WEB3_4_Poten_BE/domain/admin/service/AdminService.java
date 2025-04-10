package com.beanSpot.WEB3_4_Poten_BE.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.service.ApplicationService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

	private final MemberRepository memberRepository;
	private final ApplicationService applicationService;
	private final ApplicationRepository applicationRepository;

	// 대기 중인 신청 목록 조회
	public List<ApplicationRes> getPendingApplications() {
		return applicationService.getPendingRequests();
	}

	// 신청 승인 및 회원 권한 변경
	@Transactional
	public ApplicationRes approveApplication(Long applicationId) {
		// 1. 신청 정보 조회
		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ServiceException(404, "신청 정보를 찾을 수 없습니다."));

		// 2. 신청 상태 검증 (이미 처리된 신청인지 확인)
		if (application.getStatus() != Status.PENDING) {
			throw new ServiceException(400, "이미 처리된 신청입니다. 현재 상태: " + application.getStatus());
		}

		// 3. 회원 정보 조회 및 권한 검증
		Member member = application.getMember();
		if (member == null) {
			throw new ServiceException(400, "신청에 유효한 사용자 정보가 없습니다.");
		}

		// 4. 신청 정보 승인 처리 (카페 생성까지 포함)
		ApplicationRes result = applicationService.approveCafe(applicationId);

		// 5. 회원 권한 업데이트
		if (member.getMemberType() == Member.MemberType.OWNER) {
			log.info("회원 ID: {}는 이미 OWNER 권한을 가지고 있습니다.", member.getId());
		} else {
			// OWNER로 권한 변경 - Member 클래스의 전용 메서드 사용
			log.info("회원 ID: {}의 권한을 {}에서 OWNER로 변경합니다.", member.getId(), member.getMemberType());
			member.changeRoleToOwner();
			// JPA의 변경 감지 기능으로 인해 명시적 save 호출이 불필요할 수 있으나, 명확성을 위해 유지
			memberRepository.save(member);
		}

		return result;
	}

	// 신청 거절
	@Transactional
	public ApplicationRes rejectApplication(Long applicationId) {
		return applicationService.rejectCafe(applicationId);
	}

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
			.orElseThrow(() -> new ServiceException(404, "회원을 찾을 수 없습니다."));
	}
}