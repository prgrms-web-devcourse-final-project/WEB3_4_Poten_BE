package com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req;

import jakarta.validation.constraints.NotNull;

public record ReviewCreateReq(
	@NotNull
	Long userId,

	@NotNull
	Long cafeId,

	@NotNull
	Integer rating,

	@NotNull
	String comment

) {
}
