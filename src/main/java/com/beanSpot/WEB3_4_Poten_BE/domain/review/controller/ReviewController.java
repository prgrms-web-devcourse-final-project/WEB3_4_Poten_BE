package com.beanSpot.WEB3_4_Poten_BE.domain.review.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.service.ReviewService;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Review", description = "Review Controller")
@RestController
@RequestMapping("/api/cafes/{cafeId}/reviews")
@RequiredArgsConstructor
public class ReviewController {

	private final ReviewService reviewService;
	private final UserRepository userRepository;

	@Operation(
		summary = "리뷰 추가",
		description = "리뷰를 추가합니다.")
	//추후 인증 방식에 따라서 수정 필요.
	@PostMapping
	public ResponseEntity<ReviewRes> addReview(@PathVariable Long cafeId, @RequestBody ReviewCreateReq request) {
		request = new ReviewCreateReq(request.userId(), cafeId, request.rating(), request.comment());
		ReviewRes reviewRes = reviewService.addReview(request,1L);
		return ResponseEntity.ok(reviewRes);
		// TODO: 인증 구현 후 userId는 RequestBody에서 제거하고 SecurityContext에서 가져오기
	}

	@Operation(
		summary = "카페 수정",
		description = "리뷰를 수정합니다")
	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewRes> updateReview(@PathVariable Long cafeId, @PathVariable Long reviewId,
		@RequestBody ReviewUpdateReq request) {
		ReviewRes reviewRes = reviewService.updateReview(reviewId, request);
		return ResponseEntity.ok(reviewRes);
	}

	@Operation(
		summary = "리뷰 삭제",
		description = "리뷰를 삭제합니다")
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long cafeId, @PathVariable Long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}

	@Operation(
		summary = "카페별 리뷰 조회",
		description = "카페별 리뷰 리스트들을 반환합니다.")
	@GetMapping
	public ResponseEntity<List<ReviewRes>> getReviewsByCafe(@PathVariable Long cafeId) {
		List<ReviewRes> reviews = reviewService.getReviewsByCafeId(cafeId);
		return ResponseEntity.ok(reviews);
	}
}
