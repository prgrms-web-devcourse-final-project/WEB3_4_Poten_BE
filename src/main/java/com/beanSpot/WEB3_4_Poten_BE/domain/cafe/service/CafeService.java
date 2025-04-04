package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

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

	@Transactional
	public CafeInfoRes createCafe(CafeCreateReq request, Long ownerId) {
		User owner = userRepository.findById(ownerId)
			.orElseThrow(() -> new UserNotFoundException(ownerId));

		if (owner.getRole() != UserRole.ROLE_OWNER) {
			owner.changeRoleToOwner();
		}

		Cafe cafe = Cafe.builder()
			.owner(owner)
			.name(request.name())
			.address(request.address())
			.latitude(request.latitude())
			.longitude(request.longitude())
			.phone(request.phone())
			.description(request.description())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.image(request.image())
			.disabled(request.disabled())
			.build();

		cafeRepository.save(cafe);
		return CafeInfoRes.fromEntity(cafe);
	}

	@Transactional
	public List<CafeInfoRes> getCafeList() {
		return cafeRepository.findByDisabledFalse().stream()
			.map(CafeInfoRes::fromEntity)
			.collect(Collectors.toList());
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
	public void deleteCafe(Long id) {
		Cafe cafe = cafeRepository.findById(id)
			.orElseThrow(() -> new CafeNotFoundException(id));

		cafe.setDisabled(true);
		cafeRepository.save(cafe);
	}

}
