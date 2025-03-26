package com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;

public record ApplicationRes(Long id, String name, String address, String phone, String status) {

	public static ApplicationRes fromEntity(Application application) {
		return new ApplicationRes(
			application.getId(),
			application.getName(),
			application.getAddress(),
			application.getPhone(),
			application.getStatus().name()
		);
	}
}
