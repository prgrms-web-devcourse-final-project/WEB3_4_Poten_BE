package com.beanSpot.WEB3_4_Poten_BE.domain.member.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
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
		List<Long> cafeIds = List.of(); // 기본적으로 빈 리스트

		try {
			if (member.getOwnedCafes() != null) {
				cafeIds = member.getOwnedCafes().stream()
					.map(Cafe::getCafeId)
					.collect(Collectors.toList());
			}
		} catch (Exception e) {
			// 지연 로딩 예외 발생 시 무시하고 빈 리스트 사용
		}

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
			cafeIds
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