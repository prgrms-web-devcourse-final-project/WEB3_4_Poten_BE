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

@RestController
@RequestMapping("/api/cafes/{cafeId}/reviews")
public class ReviewController {
	@Autowired
	private ReviewService reviewService;

	@PostMapping
	public ResponseEntity<ReviewRes> addReview(@PathVariable Long cafeId, @RequestBody ReviewCreateReq request) {
		request = new ReviewCreateReq(request.userId(), cafeId, request.rating(), request.comment());
		ReviewRes reviewRes = reviewService.addReview(request);
		return ResponseEntity.ok(reviewRes);
	}

	@PutMapping("/{reviewId}")
	public ResponseEntity<ReviewRes> updateReview(@PathVariable Long cafeId, @PathVariable Long reviewId,
		@RequestBody ReviewUpdateReq request) {
		ReviewRes reviewRes = reviewService.updateReview(reviewId, request);
		return ResponseEntity.ok(reviewRes);
	}

	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(@PathVariable Long cafeId, @PathVariable Long reviewId) {
		reviewService.deleteReview(reviewId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping
	public ResponseEntity<List<ReviewRes>> getReviewsByCafe(@PathVariable Long cafeId) {
		List<ReviewRes> reviews = reviewService.getReviewsByCafeId(cafeId);
		return ResponseEntity.ok(reviews);
	}
}
