package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeInfoRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
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
	public List<CafeInfoRes> getCafeList() {
		return cafeRepository.findAll().stream()
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
}
