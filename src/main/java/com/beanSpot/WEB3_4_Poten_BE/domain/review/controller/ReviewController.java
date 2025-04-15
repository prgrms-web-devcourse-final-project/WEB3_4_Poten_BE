package com.beanSpot.WEB3_4_Poten_BE.domain.review.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "Review Controller")
@RestController
@RequestMapping("/api/cafes/{cafeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final MemberRepository memberRepository;

	@Operation(
		summary = "리뷰 수정",
		description = "리뷰를 수정합니다")
	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewRes> updateReview(
		@PathVariable Long cafeId,
		@PathVariable Long reviewId,
		@RequestBody ReviewUpdateReq request,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long userId = securityUser.getMember().getId();
		ReviewRes reviewRes = reviewService.updateReview(reviewId, userId, request);
		return ResponseEntity.ok(reviewRes);
	}

	@Operation(
		summary = "리뷰 삭제",
		description = "리뷰를 삭제합니다")
	public ResponseEntity<Void> deleteReview(
		@PathVariable Long cafeId,
		@PathVariable Long reviewId,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long userId = securityUser.getMember().getId();
		reviewService.deleteReview(reviewId, userId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "카페별 리뷰 리스트 조회 (페이징)",
		description = "카페별 리뷰 리스트를 페이징으로 반환합니다. page, size 파라미터를 사용할 수 있습니다."
	)
	@GetMapping
	public ResponseEntity<Page<ReviewRes>> getReviewsByCafe(
		@PathVariable Long cafeId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "5") int size
	) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
		Page<ReviewRes> reviews = reviewService.getReviewsByCafeId(cafeId, pageable);
		return ResponseEntity.ok(reviews);
	}
//이미지관련 리턴 통일,  리뷰, 예약 줄때 이름으로
	//TODO: 인증 구현 후 userId는 RequestBody에서 제거하고 SecurityContext에서 가져오기
	@PostMapping
	public ResponseEntity<ReviewRes> addReview(
		@PathVariable Long cafeId,
		@RequestBody ReviewCreateReq request,
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long userId = securityUser.getMember().getId();
		ReviewRes reviewRes = reviewService.addReview(cafeId, userId, request);
		return ResponseEntity.ok(reviewRes);
	}
}

