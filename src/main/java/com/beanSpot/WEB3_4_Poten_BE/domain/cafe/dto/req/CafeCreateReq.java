package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req;

import java.time.LocalDateTime;

public record CafeCreateReq(
	String name,
	String address,
	Double latitude,
	Double longitude,
	String phone,
	String description,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	String image,
	Boolean disabled
) {
}
