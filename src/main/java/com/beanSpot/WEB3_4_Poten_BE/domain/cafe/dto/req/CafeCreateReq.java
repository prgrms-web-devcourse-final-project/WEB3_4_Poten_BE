package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotEmpty;

public record CafeCreateReq(

	@NotEmpty
	String name,

	@NotEmpty
	String address,

	@NotEmpty
	Double latitude,

	@NotEmpty
	Double longitude,

	@NotEmpty
	String phone,

	@NotEmpty
	String description,

	@NotEmpty
	LocalDateTime createdAt,

	LocalDateTime updatedAt,

	@NotEmpty
	String image,

	@NotEmpty
	Boolean disabled

) {
}
