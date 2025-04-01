package com.beanSpot.WEB3_4_Poten_BE.domain.admin.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
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
	public ApplicationApprovedRes approveApplication(Long applicationId) {

		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ServiceException(404, "신청 정보를 찾을 수 없습니다."));
		// 신청 정보 조회 및 승인 (카페 생성까지 포함)
		ApplicationApprovedRes result = applicationService.approveCafe(applicationId);

		Long userId = application.getUserId();
		if (userId == null) {
			throw new ServiceException(400, "신청에 유효한 사용자 정보가 없습니다.");
		}

		// 회원 정보 조회 및 권한 업데이트
		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new ServiceException(404, "회원을 찾을 수 없습니다."));

		if (member.getMemberType() == Member.MemberType.OWNER) {
			log.info("회원 ID: {}는 이미 OWNER 권한을 가지고 있습니다.", userId);
		} else {
			// OWNER로 권한 변경
			log.info("회원 ID: {}의 권한을 {}에서 OWNER로 변경합니다.", userId, member.getMemberType());
			member.setMemberType(Member.MemberType.OWNER);
			memberRepository.save(member);
		}
		return result;
	}

	// 신청 거절
	@Transactional
	public ApplicationRes rejectApplication(Long applicationId) {
		return applicationService.rejectCafe(applicationId);
	}

}
