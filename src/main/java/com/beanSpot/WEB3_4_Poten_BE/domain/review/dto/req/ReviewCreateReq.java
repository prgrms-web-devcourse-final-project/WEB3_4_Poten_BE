package com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.req;

import jakarta.validation.constraints.NotNull;

public record ReviewCreateReq(
	@NotNull
	Long userId,//이부분 @AuthenticationPrincipal에서 가져와서 필요가 없음

	@NotNull
	Long cafeId,

	@NotNull
	Integer rating,

	@NotNull
	String comment

) {
}
