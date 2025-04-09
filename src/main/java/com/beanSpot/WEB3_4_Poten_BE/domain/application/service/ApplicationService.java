package com.beanSpot.WEB3_4_Poten_BE.domain.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req.ApplicationReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.exception.ApplicationNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {
	private final ApplicationRepository applicationRepository;
	private final CafeRepository cafeRepository;
	private final MemberRepository memberRepository;
	// private final UserRepository userRepository;

	@Transactional
	public ApplicationRes createApplication(ApplicationReq request, Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ServiceException(400, "사용자를 찾을 수 없습니다."));

		Application application = Application.builder()
			.member(member)  // user 대신 member로 변경
			.name(request.name())
			.address(request.address())
			.phone(request.phone())
			.status(Status.PENDING)
			.createdAt(LocalDateTime.now())
			.build();

		applicationRepository.save(application);
		return ApplicationRes.fromEntity(application);
	}

	@Transactional
	public void deleteRejectedApplication(Long applicationId) {
		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ApplicationNotFoundException(applicationId));

		if (application.getStatus() == Status.REJECTED) {
			applicationRepository.delete(application);
		} else {
			throw new IllegalStateException("거부된 신청이 아닙니다.");
		}
	}

	public List<ApplicationRes> getPendingRequests() {
		return applicationRepository.findByStatus(Status.PENDING)
			.stream()
			.map(ApplicationRes::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public ApplicationRes rejectCafe(Long applicationId) {
		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ApplicationNotFoundException(applicationId));

		application.reject();
		applicationRepository.save(application);

		return ApplicationRes.fromEntity(application);
	}
}

