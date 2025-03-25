package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.CafeUpdateRequest;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception.CafeNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CafeService {

	private final CafeRepository cafeRepository;

	@Transactional
	public Cafe updateCafe(Long id, CafeUpdateRequest request) {
		Cafe cafe = cafeRepository.findById(id)
			.orElseThrow(() -> new CafeNotFoundException(id));

		Cafe updatedCafe = Cafe.builder()
			.cafeId(cafe.getCafeId())
			.ownerId(cafe.getOwnerId())
			.name(request.name() != null ? request.name() : cafe.getName())
			.address(request.address() != null ? request.address() : cafe.getAddress())
			.phone(request.phone() != null ? request.phone() : cafe.getPhone())
			.description(request.description() != null ? request.description() : cafe.getDescription())
			.image(request.image() != null ? request.image() : cafe.getImage())
			.latitude(cafe.getLatitude())
			.longitude(cafe.getLongitude())
			.createdAt(cafe.getCreatedAt())
			.updatedAt(LocalDateTime.now())
			.disabled(cafe.getDisabled())
			.build();

		return cafeRepository.save(updatedCafe);
	}
}
