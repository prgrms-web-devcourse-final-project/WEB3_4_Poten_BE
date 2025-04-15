package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

public record CafeApplicationRes(
	Long id,
	String name,
	String address,
	String phone,
	int capacity,
	String status,
	LocalDateTime createdAt

) {
	public static CafeApplicationRes fromEntity(Cafe cafe, Application application) {
		return new CafeApplicationRes(
			application.getId(),
			application.getName(),
			application.getAddress(),
			application.getPhone(),
			application.getCapacity(),
			application.getStatus().name(),
			application.getCreatedAt()
		);
	}
}

