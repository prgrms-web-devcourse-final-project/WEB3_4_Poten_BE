package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

public record ResponseMemberMyPageDto(
	Long id,
	String oAuthId,
	String name,
	String email,
	Member.MemberType memberType,
	Member.SnsType snsType,
	String profileImg,
	String phoneNumber,
	String updatedAt
) {
	public static ResponseMemberMyPageDto from(Member entity) {
		return new ResponseMemberMyPageDto(
			entity.getId(),
			entity.getOAuthId(),
			entity.getName(),
			entity.getEmail(),
			entity.getMemberType(),
			entity.getSnsType(),
			entity.getProfileImg(),
			entity.getPhoneNumber(),
			java.time.LocalDateTime.now().toString()
		);
	}
}

