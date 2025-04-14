package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeDetailRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res.CafeInfoRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service.CafeService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;

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
		summary = "카페 수정",
		description = "카페를 수정합니다. 이름, 주소, 전화번호, 설명, 이미지 데이터를 넘겨받습니다.")
	@PutMapping("/{id}")
	public ResponseEntity<CafeInfoRes> updateCafe(
		@PathVariable Long id,
		@RequestBody CafeUpdateReq request,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long userId = securityUser.getMember().getId();
		CafeInfoRes updatedCafe = cafeService.updateCafe(id, userId, request);
		return ResponseEntity.ok(updatedCafe);
	}

	@Operation(
		summary = "카페 리스트 (페이징)",
		description = "존재하는 카페의 리스트를 페이징 형태로 반환합니다. page, size, sort 파라미터를 사용할 수 있습니다."
	)
	@GetMapping
	public ResponseEntity<Page<CafeInfoRes>> getCafeList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt,desc") String sort
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		Page<CafeInfoRes> result = cafeService.getCafeList(pageable);
		return ResponseEntity.ok(result);
	}
	@Operation(
		summary = "카페 검색",
		description = "주어진 키워드를 기반으로 카페를 검색해 검색된 카페 목록은 카페의 정보를 포함한 리스트로 반환됩니다. 검색은 카페의 이름과 주소로 이루어집니다.")
	@GetMapping("/search")
	public ResponseEntity<List<CafeInfoRes>> searchCafe(@RequestParam String keyword) {
		List<CafeInfoRes> result = cafeService.searchCafe(keyword);
		return ResponseEntity.ok(result);
	}

	@Operation(
		summary = "카페 삭제",
		description = "카페를 삭제합니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCafe(
		@PathVariable Long id,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long userId = securityUser.getMember().getId();
		cafeService.deleteCafe(id, userId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@Operation(
		summary = "카페 상세 조회",
		description = "카페의 상세 정보를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<CafeDetailRes> getCafeDetail(@PathVariable Long id) {
		CafeDetailRes cafeDetailRes = cafeService.getCafeDetail(id);
		return ResponseEntity.ok(cafeDetailRes);
	}

	//TODO: 인증 구현 후 userId는 RequestBody에서 제거하고 SecurityContext에서 가져오기
	@PostMapping
	public ResponseEntity<CafeInfoRes> createCafe(
		@RequestBody CafeCreateReq cafeCreateReq,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		CafeInfoRes cafeInfoRes = cafeService.createCafe(cafeCreateReq, securityUser.getMember().getId());
		return ResponseEntity.ok(cafeInfoRes);
	}
}

