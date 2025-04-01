package com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

public record ApplicationApprovedRes(
	@NonNull
	Long id,

	@NonNull
	Long ownerId,

	@NotEmpty
	String name,

	@NotEmpty
	String address,

	@NotEmpty
	String phone



) {

	public static ApplicationApprovedRes fromEntity(Cafe cafe) {
		return new ApplicationApprovedRes(
			cafe.getCafeId(),
			cafe.getOwner().getId(),
			cafe.getName(),
			cafe.getAddress(),
			cafe.getPhone()
			// cafe.getOwner().getId()
		);
	}
}