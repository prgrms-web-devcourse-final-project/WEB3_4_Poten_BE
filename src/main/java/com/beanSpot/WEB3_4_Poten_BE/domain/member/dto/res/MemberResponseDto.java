package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

public record MemberResponseDto(
	Long id,
	String email,
	String name,
	Member.MemberType memberType
) {
	// 엔티티에서 DTO로 변환하는 팩토리 메서드
	public static MemberResponseDto fromEntity(Member member) {
		return new MemberResponseDto(
			member.getId(),
			member.getEmail(),
			member.getName(),
			member.getMemberType()
		);
	}
}