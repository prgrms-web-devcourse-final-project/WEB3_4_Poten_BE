package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMemberMyPageDto {
	@NotBlank(message = "이름은 필수 입력값입니다.")
	private String name;

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	private String email;

	@Pattern(regexp = "^\\d{10,11}$", message = "전화번호는 10-11자리 숫자여야 합니다.")
	private String phoneNumber;
}
