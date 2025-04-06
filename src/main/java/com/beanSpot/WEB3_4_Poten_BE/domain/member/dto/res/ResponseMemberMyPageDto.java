package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res;

import java.time.LocalDateTime;

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
	LocalDateTime createdAt,
	LocalDateTime updatedAt
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
			entity.getCreatedAt(),
			entity.getUpdatedAt()
		);
	}
}


