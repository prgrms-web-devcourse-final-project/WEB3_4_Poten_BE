package com.beanSpot.WEB3_4_Poten_BE.domain.review.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
	public ReviewRes addReview(Long cafeId, Long userId, ReviewCreateReq request) {

		Member member = memberRepository.findById(userId)
			.orElseThrow(() -> new ServiceException(400, "사용자를 찾을 수 없습니다."));

		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		Review review = Review.builder()
			.member(member)
			.cafe(cafe)
			.rating(request.rating())
			.comment(request.comment())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		reviewRepository.save(review);
		return ReviewRes.fromEntity(review);
	}

	@Transactional
	public ReviewRes updateReview(Long reviewId, Long userId, ReviewUpdateReq request) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));

		if (!review.getMember().getId().equals(userId)) {
			throw new ServiceException(403, "본인이 작성한 리뷰만 수정할 수 있습니다.");
		}

		review.updateReview(
			request.rating(),
			request.comment());

		return ReviewRes.fromEntity(review);
	}

	public void deleteReview(Long reviewId,Long userId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ReviewNotFoundException(reviewId));

		if (!review.getMember().getId().equals(userId)) {
			throw new ServiceException(403, "본인이 작성한 리뷰만 삭제할 수 있습니다.");
		}

		reviewRepository.delete(review);
	}

	@Transactional
	public Page<ReviewRes> getReviewsByCafeId(Long cafeId, Pageable pageable) {
		Cafe cafe = cafeRepository.findById(cafeId)
			.orElseThrow(() -> new CafeNotFoundException(cafeId));

		Page<Review> reviews = reviewRepository.findByCafe(cafe, pageable);

		return reviews.map(ReviewRes::fromEntity);
	}

	public List<ReviewRes> getReviewsByOwner(Long ownerId) {
		List<Review> reviews = reviewRepository.findByOwnerId(ownerId);
		return reviews.stream()
			.map(ReviewRes::fromEntity)
			.collect(Collectors.toList());
	}
}
