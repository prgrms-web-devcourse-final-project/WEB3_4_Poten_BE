package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;

@Builder
public record CafeInfoResponse(
	@NonNull
	Long cafeId,
	@NonNull
	Long ownerId,
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
