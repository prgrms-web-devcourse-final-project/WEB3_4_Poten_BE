package com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req;

import jakarta.validation.constraints.NotEmpty;

public record ApplicationReq(
	@NotEmpty
	String name,

	@NotEmpty
	String address,

	@NotEmpty
	String phone
) {
}
