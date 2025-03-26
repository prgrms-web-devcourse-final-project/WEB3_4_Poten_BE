package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

	@PutMapping("/{id}")
	public ResponseEntity<Cafe> updateCafe(@PathVariable Long id, @RequestBody CafeUpdateReq request) {
		Cafe updatedCafe = cafeService.updateCafe(id, request);
		return ResponseEntity.ok(updatedCafe);
	}

	@GetMapping
	public List<CafeInfoRes> getCafeList() {
		return cafeService.getCafeList();
	}
}

