package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req;

public record CafeUpdateReq(
	String name,
	String address,
	String phone,
	String description,
	String image
) {
}