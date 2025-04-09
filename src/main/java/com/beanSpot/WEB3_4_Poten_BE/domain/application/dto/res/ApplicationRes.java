package com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

public record ApplicationRes(
	@NonNull
	Long id,

	@NonNull
	Long userId,

	@NotEmpty
	String name,

	@NotEmpty
	String address,

	@NotEmpty
	String phone,

	@NotEmpty
	String status
) {

	public static ApplicationRes fromEntity(Application application) {
		return new ApplicationRes(
			application.getId(),
			application.getMember().getId(),
			application.getName(),
			application.getAddress(),
			application.getPhone(),
			application.getStatus().name()
		);
	}
}
