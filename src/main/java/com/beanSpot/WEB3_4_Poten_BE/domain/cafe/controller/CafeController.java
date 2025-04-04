package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Cafe", description = "Cafe Controller")
@RestController
@RequestMapping("/api/cafes")
@RequiredArgsConstructor
public class CafeController {

	private final CafeService cafeService;

	@Operation(
		summary = "카페 추가",
		description = "카페를 추가합니다. 카페에 할당된 사용자는 OWNER 상태로 변경됩니다.")
	@PostMapping
	public ResponseEntity<CafeInfoRes> createCafe(@RequestBody CafeCreateReq cafeCreateReq) {
		CafeInfoRes cafeInfoRes = cafeService.createCafe(cafeCreateReq, 1L);
		return ResponseEntity.ok(cafeInfoRes);
	}

	@Operation(
		summary = "카페 수정",
		description = "카페를 수정합니다. 이름, 주소, 전화번호, 설명, 이미지 데이터를 넘겨받습니다.")
	@PutMapping("/{id}")
	public ResponseEntity<Cafe> updateCafe(@PathVariable("id") Long id, @RequestBody CafeUpdateReq request) {
		Cafe updatedCafe = cafeService.updateCafe(id, request);
		return ResponseEntity.ok(updatedCafe);
	}

	@Operation(
		summary = "카페 리스트 반환",
		description = "존재하는 카페의 리스트를 반환합니다.")
	@GetMapping
	public List<CafeInfoRes> getCafeList() {
		return cafeService.getCafeList();
	}

	@Operation(
		summary = "카페 검색",
		description = "주어진 키워드를 기반으로 카페를 검색해 검색된 카페 목록은 카페의 정보를 포함한 리스트로 반환됩니다. 검색은 카페의 이름과 주소로 이루어집니다.")
	@GetMapping("/search")
	public ResponseEntity<List<CafeInfoRes>> searchCafe(@RequestBody String keyword) {
		List<CafeInfoRes> result = cafeService.searchCafe(keyword);
		return ResponseEntity.ok(result);
	}
	@Operation(
		summary = "카페 삭제",
		description = "카페를 삭제합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCafe(@PathVariable Long id) {
		cafeService.deleteCafe(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}

