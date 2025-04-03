package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto;

import java.util.HashMap;
import java.util.Map;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

public record MemberDto(
	Long id,
	String OAuthId,
	String name,
	String email,
	Member.MemberType memberType,
	String profileImg,
	Member.SnsType snsType
) {
	public static MemberDto from(Member member) {
		return new MemberDto(
			member.getId(),
			member.getOAuthId(),
			member.getName(),
			member.getEmail(),
			member.getMemberType(),
			member.getProfileImg(),
			member.getSnsType()
		);
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", this.id);
		attributes.put("oAuthId", this.OAuthId);
		attributes.put("name", this.name);
		attributes.put("email", this.email);
		attributes.put("memberType", this.memberType);
		attributes.put("profileImg", this.profileImg);
		attributes.put("snsType", this.snsType);
		return attributes;
	}
}