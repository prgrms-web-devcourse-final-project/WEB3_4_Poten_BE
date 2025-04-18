package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res;

import lombok.Builder;
import lombok.NonNull;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

// Owner 관련 코드 추후 수정필요
@Builder
public record CafeInfoRes(
	@NonNull
	Long cafeId,
	Long ownerId,
	String name,
	String address,
	Double latitude,
	Double longitude,
	String phone,
	String description,
	int capacity,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	String image
) {

	public static CafeInfoRes fromEntity(Cafe cafe, String imageUrl) {

		return CafeInfoRes.builder()
			.cafeId(cafe.getCafeId())
			.ownerId(cafe.getOwner().getId())
			.name(cafe.getName())
			.address(cafe.getAddress())
			.latitude(cafe.getLatitude())
			.longitude(cafe.getLongitude())
			.phone(cafe.getPhone())
			.description(cafe.getDescription())
			.capacity(cafe.getCapacity())
			.createdAt(cafe.getCreatedAt())
			.updatedAt(cafe.getUpdatedAt())
			.image(imageUrl)
			.build();
	}
}


