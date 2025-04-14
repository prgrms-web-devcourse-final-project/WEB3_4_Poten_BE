package com.beanSpot.WEB3_4_Poten_BE.domain.review.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.service.ReviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/owner/reviews")
@RequiredArgsConstructor
public class OwnerReviewController {

	private final ReviewService reviewService;

	@GetMapping("/my-reviews")
	public ResponseEntity<List<ReviewRes>> getReviewsByOwner(
		@AuthenticationPrincipal SecurityUser securityUser
	) {
		Long ownerId = securityUser.getMember().getId();
		List<ReviewRes> reviews = reviewService.getReviewsByOwner(ownerId);
		return ResponseEntity.ok(reviews);
	}
}
