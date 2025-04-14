package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.ReviewAdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.exception.ReviewNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin-Review", description = "리뷰 관리 API")
public class AdminReviewController {

	private final ReviewAdminService reviewAdminService;

	@Operation(
		summary = "모든 리뷰 조회",
		description = "관리자가 모든 리뷰를 조회합니다.")
	@GetMapping("/reviews")
	public ResponseEntity<List<ReviewRes>> getAllReviews() {
		try {
			List<ReviewRes> reviews = reviewAdminService.getAllReviews();
			return ResponseEntity.ok(reviews);
		} catch (Exception e) {
			log.error("리뷰 목록 조회 중 오류 발생", e);
			throw new ServiceException("리뷰 목록 조회 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "특정 회원의 리뷰 조회",
		description = "관리자가 특정 회원의 리뷰를 조회합니다.")
	@GetMapping("/members/{memberId}/reviews")
	public ResponseEntity<List<ReviewRes>> getMemberReviews(@PathVariable Long memberId) {
		try {
			List<ReviewRes> reviews = reviewAdminService.getReviewsByMemberId(memberId);
			return ResponseEntity.ok(reviews);
		} catch (ServiceException e) {
			log.error("회원 리뷰 조회 중 오류 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("회원 리뷰 조회 중 예상치 못한 오류 발생", e);
			throw new ServiceException("회원 리뷰 조회 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "리뷰 삭제",
		description = "관리자가 특정 리뷰를 삭제합니다.")
	@DeleteMapping("/reviews/{reviewId}")
	public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long reviewId) {
		try {
			reviewAdminService.deleteReviewByAdmin(reviewId);
			return ResponseEntity.ok(Map.of("message", "리뷰가 성공적으로 삭제되었습니다."));
		} catch (ReviewNotFoundException e) {
			log.error("리뷰 삭제 중 오류 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("리뷰 삭제 중 예상치 못한 오류 발생", e);
			throw new ServiceException("리뷰 삭제 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "여러 리뷰 일괄 삭제",
		description = "관리자가 여러 리뷰를 한 번에 삭제합니다.")
	@DeleteMapping("/reviews")
	public ResponseEntity<Map<String, String>> deleteMultipleReviews(@RequestBody List<Long> reviewIds) {
		try {
			reviewAdminService.deleteReviewsByAdmin(reviewIds);
			return ResponseEntity.ok(Map.of("message", "선택한 리뷰들이 성공적으로 삭제되었습니다."));
		} catch (Exception e) {
			log.error("리뷰 일괄 삭제 중 오류 발생", e);
			throw new ServiceException("리뷰 일괄 삭제 실패: " + e.getMessage());
		}
	}

}
