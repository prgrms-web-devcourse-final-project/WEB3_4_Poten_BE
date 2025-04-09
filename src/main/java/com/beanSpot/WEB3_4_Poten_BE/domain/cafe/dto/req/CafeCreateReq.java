package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req;

import jakarta.validation.constraints.NotEmpty;

public record CafeCreateReq(

	@NotEmpty
	String name,

	@NotEmpty
	String address,

	@NotEmpty
	String phone,

	@NotEmpty
	String description,

	@NotEmpty
	String imageFilename
) {
}
