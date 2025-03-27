package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeInfoRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service.CafeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

	private final CafeService cafeService;

	@PostMapping
	public ResponseEntity<CafeInfoRes> createCafe(@RequestBody CafeCreateReq cafeCreateReq) {
		CafeInfoRes cafeInfoRes = cafeService.createCafe(cafeCreateReq);
		return ResponseEntity.ok(cafeInfoRes);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Cafe> updateCafe(@PathVariable Long id, @RequestBody CafeUpdateReq request) {
		Cafe updatedCafe = cafeService.updateCafe(id, request);
		return ResponseEntity.ok(updatedCafe);
	}

	@GetMapping
	public List<CafeInfoRes> getCafeList() {
		return cafeService.getCafeList();
	}

	@GetMapping("/search")
	public ResponseEntity<List<CafeInfoRes>> searchCafe(@RequestBody String keyword) {
		List<CafeInfoRes> result = cafeService.searchCafe(keyword);
		return ResponseEntity.ok(result);
	}
}

