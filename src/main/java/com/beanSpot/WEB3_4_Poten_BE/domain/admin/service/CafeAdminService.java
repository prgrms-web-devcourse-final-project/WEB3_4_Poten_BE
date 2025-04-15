package com.beanSpot.WEB3_4_Poten_BE.domain.admin.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.service.ApplicationService;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CafeAdminService {

	private final MemberRepository memberRepository;
	private final ApplicationService applicationService;
	private final ApplicationRepository applicationRepository;
	private final ReviewRepository reviewRepository;
	private final CafeRepository cafeRepository;

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

		// 3. 회원 정보 조회 및 검증
		Member member = application.getMember();
		if (member == null) {
			throw new ServiceException(400, "신청에 유효한 사용자 정보가 없습니다.");
		}

		// 4. 신청 상태를 승인으로 변경
		application.approve();
		applicationRepository.save(application);

		// 5. 회원 권한 업데이트
		if (member.getMemberType() != Member.MemberType.OWNER) {
			log.info("회원 ID: {}의 권한을 {}에서 OWNER로 변경합니다.", member.getId(), member.getMemberType());
			member.changeRoleToOwner();
			memberRepository.save(member);
		} else {
			log.info("회원 ID: {}는 이미 OWNER 권한을 가지고 있습니다.", member.getId());
		}

		// 6. 카페 엔티티 생성 및 저장
		try {
			Cafe cafe = Cafe.builder()
				.owner(member)
				.application(application)
				.name(application.getName())
				.address(application.getAddress())
				.latitude(0.0) // 기본값 설정 (추후 업데이트 필요)
				.longitude(0.0) // 기본값 설정 (추후 업데이트 필요)
				.phone(application.getPhone())
				.description("") // 기본 설명 (추후 업데이트 필요)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.image("default-cafe.jpg") // 기본 이미지 (추후 업데이트 필요)
				.capacity(application.getCapacity())
				.disabled(false)
				.build();

			cafeRepository.save(cafe);
			log.info("카페가 성공적으로 생성되었습니다. 카페 ID: {}, 이름: {}", cafe.getCafeId(), cafe.getName());
		} catch (Exception e) {
			log.error("카페 생성 중 오류 발생: {}", e.getMessage(), e);
			throw new ServiceException(500, "카페 생성 중 오류가 발생했습니다: " + e.getMessage());
		}

		// 7. 승인 결과 반환
		return ApplicationRes.fromEntity(application);
	}

	// 신청 거절
	@Transactional
	public ApplicationRes rejectApplication(Long applicationId) {
		return applicationService.rejectCafe(applicationId);
	}

}