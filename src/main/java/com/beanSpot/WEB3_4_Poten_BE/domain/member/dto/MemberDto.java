package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto;

import java.util.HashMap;
import java.util.Map;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberDto {
	private Long id;
	private String oAuthId;
	private String name;
	private String email;
	private Member.MemberType memberType;
	private String profileImg;
	private Member.SnsType snsType;

	public MemberDto(Member entity) {
		this.id = entity.getId();
		this.oAuthId = entity.getOAuthId();
		this.name = entity.getName();
		this.email = entity.getEmail();
		this.memberType = entity.getMemberType();
		this.snsType = entity.getSnsType();
		this.profileImg = entity.getProfileImg();
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", this.id);
		attributes.put("oAuthId", this.oAuthId);
		attributes.put("name", this.name);
		attributes.put("email", this.email);
		attributes.put("memberType", this.memberType);
		attributes.put("profileImg", this.profileImg);
		attributes.put("snsType", this.snsType);
		return attributes;
	}

}
