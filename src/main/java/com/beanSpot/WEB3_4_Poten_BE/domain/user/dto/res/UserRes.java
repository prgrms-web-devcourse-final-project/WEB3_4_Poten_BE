package com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;

import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

public record UserRes(
	@NonNull
	Long id,

	@NotEmpty
	String name,

	@NotEmpty
	String email,

	@NotEmpty
	String role,

	@NotEmpty
	LocalDateTime createdAt,

	@NotEmpty
	LocalDateTime updatedAt,

	@NotEmpty
	String oAuthId,

	@NotEmpty
	String snsType,

	@NotEmpty
	String profileImageName
) {

	public static UserRes fromEntity(User user) {
		return new UserRes(
			user.getId(),
			user.getName(),
			user.getEmail(),
			user.getRole(),
			user.getCreatedAt(),
			user.getUpdatedAt(),
			user.getOAuthId(),
			user.getSnsType() != null ? user.getSnsType().name() : null,
			user.getProfileImageName()
		);
	}
}
