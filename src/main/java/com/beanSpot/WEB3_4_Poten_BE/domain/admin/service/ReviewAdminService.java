package com.beanSpot.WEB3_4_Poten_BE.domain.admin.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.repository.ReviewRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewAdminService {

	private final MemberRepository memberRepository;
	private final ReviewRepository reviewRepository;

	public List<ReviewRes> getAllReviews() {
		List<Review> reviews = reviewRepository.findAll();
		return reviews.stream()
			.map(ReviewRes::fromEntity)
			.collect(Collectors.toList());
	}

	public List<ReviewRes> getReviewsByMemberId(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new ServiceException(400, "사용자를 찾을 수 없습니다."));

		List<Review> reviews = reviewRepository.findByMember(member);
		return reviews.stream()
			.map(ReviewRes::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public void deleteReviewByAdmin(Long reviewId) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new ServiceException(400, "리뷰를 찾을 수 없습니다."));
		reviewRepository.delete(review);
	}


	@Transactional
	public void deleteReviewsByAdmin(List<Long> reviewIds) {
		reviewRepository.deleteByIdIn(reviewIds);
	}

}