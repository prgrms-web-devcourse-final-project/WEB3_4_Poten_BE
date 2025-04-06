package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

public record MemberDto(
	Long id,
	String oAuthId,
	String name,
	String email,
	String phoneNumber,
	Member.MemberType memberType,
	String profileImg,
	Member.SnsType snsType,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	List<Long> ownedCafeIds
) {
	public static MemberDto from(Member member) {
		return new MemberDto(
			member.getId(),
			member.getOAuthId(),
			member.getName(),
			member.getEmail(),
			member.getPhoneNumber(),
			member.getMemberType(),
			member.getProfileImg(),
			member.getSnsType(),
			member.getCreatedAt(),
			member.getUpdatedAt(),
			member.getOwnedCafes() != null
				? member.getOwnedCafes().stream()
				.map(cafe -> cafe.getCafeId())
				.collect(Collectors.toList())
				: List.of()
		);
	}

	public Map<String, Object> getAttributes() {
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("id", this.id);
		attributes.put("oAuthId", this.oAuthId);
		attributes.put("name", this.name);
		attributes.put("email", this.email);
		attributes.put("phoneNumber", this.phoneNumber);
		attributes.put("memberType", this.memberType);
		attributes.put("profileImg", this.profileImg);
		attributes.put("snsType", this.snsType);
		attributes.put("createdAt", this.createdAt);
		attributes.put("updatedAt", this.updatedAt);
		attributes.put("ownedCafeIds", this.ownedCafeIds);
		return attributes;
	}
}