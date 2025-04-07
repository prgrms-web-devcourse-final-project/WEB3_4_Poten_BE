package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Status;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeInfoRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception.CafeNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.UserRole;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.exception.UserNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeService {

	private final CafeRepository cafeRepository;
	private final UserRepository userRepository;
	private final ApplicationRepository applicationRepository;

	@Transactional
	public CafeInfoRes createCafe(CafeCreateReq request, Long ownerId) {
		User owner = userRepository.findById(ownerId)
			.orElseThrow(() -> new UserNotFoundException(ownerId));

		if (owner.getRole() != UserRole.ROLE_OWNER) {
			owner.changeRoleToOwner();
		}

		//승인된 신청이 있어야만 카페 생성 가능.
		Application approvedApplication = applicationRepository.findByUserIdAndStatus(ownerId, Status.APPROVED)
			.orElseThrow(() -> new IllegalStateException("승인된 신청이 없습니다."));

		Cafe cafe = Cafe.builder()
			.owner(owner)
			.application(approvedApplication)
			.name(request.name())
			.address(request.address())
			.latitude(0.0) //추후수정
			.longitude(0.0) //추후 수정
			.phone(request.phone())
			.description(request.description())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.image("url") //추후 수정
			.capacity(0) //추후 수정
			.disabled(false)
			.build();

		cafeRepository.save(cafe);

		return CafeInfoRes.fromEntity(cafe);
	}

	@Transactional
	public List<CafeInfoRes> getCafeList() {
		return cafeRepository.findAllByDisabledFalse().stream()
			.map(CafeInfoRes::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public CafeInfoRes getCafeDetail(Long cafeId) {
		Cafe cafe = cafeRepository.findBycafeIdAndDisabledFalse(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		return CafeInfoRes.fromEntity(cafe);
	}

	@Transactional
	public Cafe updateCafe(Long id, CafeUpdateReq request) {
		Cafe cafe = cafeRepository.findById(id)
			.orElseThrow(() -> new CafeNotFoundException(id));

		cafe.update(request);

		return cafeRepository.save(cafe);
	}

	//일단 CafeInfoRes dto 활용, 추후 필요에 따라 변경 가능
	@Transactional
	public List<CafeInfoRes> searchCafe(String keyword) {
		if (keyword == null || keyword.trim().isEmpty()) {
			throw new IllegalStateException("검색 키워드는 비워둘 수 없습니다.");
		}

		List<Cafe> cafes = cafeRepository.searchByKeywordAndDisabledFalse(keyword);

		return cafes.stream()
			.map(CafeInfoRes::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteCafe(Long cafeId) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		// Application 삭제
		applicationRepository.delete(cafe.getApplication());

		// Cafe soft delete 처리
		cafe.disable();
	}
}