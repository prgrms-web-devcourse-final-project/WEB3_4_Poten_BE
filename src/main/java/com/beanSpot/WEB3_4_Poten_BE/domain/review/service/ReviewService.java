package com.beanSpot.WEB3_4_Poten_BE.domain.review.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception.CafeNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req.ReviewUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.exception.ReviewNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final MemberRepository memberRepository;
	private final CafeRepository cafeRepository;

	@Transactional
	public ReviewRes addReview(ReviewCreateReq request, Long userId) {

		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new ServiceException(400, "사용자를 찾을 수 없습니다."));

		Cafe cafe = cafeRepository.findById(request.cafeId())
			.orElseThrow(() -> new CafeNotFoundException(request.cafeId()));

		Review review = Review.builder()
			.member(member)
			.cafe(cafe)
			.rating(request.rating())
			.comment(request.comment())
			.build();

		reviewRepository.save(review);
		return ReviewRes.fromEntity(review);
	}

	@Transactional
	public ReviewRes updateReview(Long reviewId, ReviewUpdateReq request) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));

		review.updateReview(
			request.rating(),
			request.comment());

		return ReviewRes.fromEntity(review);
	}

	public void deleteReview(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));

		reviewRepository.delete(review);
	}

	public List<ReviewRes> getReviewsByCafeId(Long cafeId) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		List<Review> reviews = reviewRepository.findByCafe(cafe);
		return reviews.stream()
			.map(ReviewRes::fromEntity)
			.collect(Collectors.toList());
	}

}
