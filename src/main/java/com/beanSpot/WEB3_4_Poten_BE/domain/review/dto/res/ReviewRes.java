package com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

public record ReviewRes(
	Long id,
	Long memberId,
	Long cafeId,
	String userName,
	String cafeName,
	int rating,
	String comment,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {

	public static ReviewRes fromEntity(Review review) {
		return new ReviewRes(
			review.getId(),
			review.getMember().getId(),
			review.getCafe().getCafeId(),
			review.getMember().getName(),
			review.getCafe().getName(),
			review.getRating(),
			review.getComment(),
			review.getCreatedAt(),
			review.getUpdatedAt()
		);
	}
}
