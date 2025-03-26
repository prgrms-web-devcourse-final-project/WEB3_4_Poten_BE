package com.beanSpot.WEB3_4_Poten_BE.domain.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.exception.ApplicationNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {
	private final ApplicationRepository applicationRepository;
	private final CafeRepository cafeRepository;
	// private final UserRepository userRepository;

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

		//User updatedOwner = application.getUser().changeRoleToOwner();
		//userRepository.save(updatedOwner);

		Cafe newCafe = Cafe.builder()
			//.owner(ownerId)
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

