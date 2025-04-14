package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.dto.res.ReviewRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

public record CafeDetailRes(
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
	public static CafeDetailRes fromEntity(Cafe cafe, String imageUrl) {
		return new CafeDetailRes(
			cafe.getCafeId(),
			cafe.getOwner().getId(),
			cafe.getName(),
			cafe.getAddress(),
			cafe.getLatitude(),
			cafe.getLongitude(),
			cafe.getPhone(),
			cafe.getDescription(),
			cafe.getCapacity(),
			cafe.getCreatedAt(),
			cafe.getUpdatedAt(),
			imageUrl
		);
	}
}
