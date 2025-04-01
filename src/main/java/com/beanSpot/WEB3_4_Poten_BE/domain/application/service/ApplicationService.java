package com.beanSpot.WEB3_4_Poten_BE.domain.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req.ApplicationReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.exception.ApplicationNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.exception.UserNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {
	private final ApplicationRepository applicationRepository;
	private final CafeRepository cafeRepository;
	private final UserRepository userRepository;
	// private final UserRepository userRepository;

	@Transactional
	public ApplicationRes createApplication(ApplicationReq request, Long userId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new UserNotFoundException(userId));

		Application application = Application.builder()
			.user(user)
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
	public ApplicationApprovedRes approveCafe(Long applicationId) {
		Application application = applicationRepository.findById(applicationId)
			.orElseThrow(() -> new ApplicationNotFoundException(applicationId));

		application.approve();

		User owner = application.getUser();
		owner.changeRoleToOwner();

		Cafe newCafe = Cafe.builder()
			.owner(owner)
			.name(application.getName())
			.address(application.getAddress())
			.phone(application.getPhone())
			.build();

		cafeRepository.save(newCafe);

		return ApplicationApprovedRes.fromEntity(newCafe);
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

