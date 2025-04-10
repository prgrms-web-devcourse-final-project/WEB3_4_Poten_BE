package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateMemberMyPageDto(
	@NotBlank(message = "이름은 필수 입력값입니다.")
	String name,

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	String email,

	@Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10-11자리 숫자여야 합니다.")
	String phoneNumber
) {
	// Builder 패턴 대체
	public static UpdateMemberMyPageDtoBuilder builder() {
		return new UpdateMemberMyPageDtoBuilder();
	}

	public static class UpdateMemberMyPageDtoBuilder {
		private String name;
		private String email;
		private String phoneNumber;

		public UpdateMemberMyPageDtoBuilder name(String name) {
			this.name = name;
			return this;
		}

		public UpdateMemberMyPageDtoBuilder email(String email) {
			this.email = email;
			return this;
		}

		public UpdateMemberMyPageDtoBuilder phoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
			return this;
		}

		public UpdateMemberMyPageDto build() {
			return new UpdateMemberMyPageDto(name, email, phoneNumber);
		}
	}
}