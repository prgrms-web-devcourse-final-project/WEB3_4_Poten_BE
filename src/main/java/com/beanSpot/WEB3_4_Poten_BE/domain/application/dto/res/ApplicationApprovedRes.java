package com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

public record ApplicationApprovedRes(
	Long id,
	String name,
	String address,
	String phone
	// Long ownerId
) {

	public static ApplicationApprovedRes fromEntity(Cafe cafe) {
		return new ApplicationApprovedRes(
			cafe.getCafeId(),
			cafe.getName(),
			cafe.getAddress(),
			cafe.getPhone()
			// cafe.getOwner().getId()
		);
	}
}