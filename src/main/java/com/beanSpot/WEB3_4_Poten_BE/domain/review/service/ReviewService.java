package com.beanSpot.WEB3_4_Poten_BE.domain.review.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.exception.ReviewNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {
	private final ReviewRepository reviewRepository;

	public ReviewRes addReview(ReviewCreateReq request) {
		Review review = Review.builder()
			.userId(request.userId())
			.cafeId(request.cafeId())
			.rating(request.rating())
			.comment(request.comment())
			.build();

		reviewRepository.save(review);
		return ReviewRes.fromEntity(review);
	}

	public ReviewRes updateReview(Long reviewId, ReviewUpdateReq request) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));
		review.updateReview(
			request.rating(),
			request.comment());

		reviewRepository.save(review);

		return ReviewRes.fromEntity(review);
	}

	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));

		reviewRepository.delete(review);
	}

	public List<ReviewRes> getReviewsByCafeId(Long cafeId) {
		List<Review> reviews = reviewRepository.findByCafeId(cafeId);
		return reviews.stream()
			.map(ReviewRes::fromEntity)
			.collect(Collectors.toList());
	}

}
