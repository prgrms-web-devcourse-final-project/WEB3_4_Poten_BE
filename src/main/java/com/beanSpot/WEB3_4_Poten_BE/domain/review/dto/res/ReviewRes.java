package com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

public record ReviewRes(
	Long id,
	Long userId,
	Long cafeId,
	int rating,
	String comment,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static ReviewRes fromEntity(Review review) {
		return new ReviewRes(
			review.getId(),
			review.getUser().getId(),
			review.getCafe().getCafeId(),
			review.getRating(),
			review.getComment(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
