package com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

import lombok.Builder;

@Builder
public record AdminCafeListRes(
	Long cafeId,
	String name,
	String address,
	String phone,
	Long ownerId,
	String ownerName,
	LocalDateTime createdAt,
	boolean disabled
) {
	public static AdminCafeListRes fromEntity(Cafe cafe) {
		return AdminCafeListRes.builder()
			.cafeId(cafe.getCafeId())
			.name(cafe.getName())
			.address(cafe.getAddress())
			.phone(cafe.getPhone())
			.ownerId(cafe.getOwner().getId())
			.ownerName(cafe.getOwner().getName())
			.createdAt(cafe.getCreatedAt())
			.disabled(cafe.isDisabled())
			.build();
	}
}