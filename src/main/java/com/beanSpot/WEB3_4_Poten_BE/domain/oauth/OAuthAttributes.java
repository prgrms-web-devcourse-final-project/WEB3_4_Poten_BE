package com.beanSpot.WEB3_4_Poten_BE.domain.oauth;

import java.util.Map;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;

@Getter
public class OAuthAttributes {
	private final Map<String, Object> attributes;
	private final String nameAttributeKey;
	private final String oAuthId;  // kakaoId를 oAuthId로 변경
	private final String name;
	private final String email;   //
	private final String profileImg;

	@Builder
	public OAuthAttributes(Map<String, Object> attributes,
		String nameAttributeKey,
		String oAuthId,
		String name,
		String email,
		String profileImg) {
		this.attributes = attributes;
		this.nameAttributeKey = nameAttributeKey;
		this.oAuthId = oAuthId;
		this.name = name;
		this.email = email;
		this.profileImg = profileImg;
	}

	public static OAuthAttributes attributes(String registrationId,
		String userNameAttributeName,
		Map<String, Object> attributes) {
		// kakao_account에서 필요한 정보 추출
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>)kakaoAccount.get("profile");

		return OAuthAttributes.builder()
			.name((String)profile.get("nickname"))
			.profileImg((String)profile.get("profile_img"))
			.email((String)kakaoAccount.get("email"))
			.oAuthId(String.valueOf(attributes.get("id")))  //
			.attributes(attributes)
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	public Member toEntity() {
		return Member.builder()
			.oAuthId(oAuthId)
			.name(name)
			.email(email)
			.phoneNumber("")
			.memberType(Member.MemberType.USER)
			.profileImg(profileImg)
			.build();
	}

	// OAuth2User의 attributes를 만들기 위한 메소드 추가
	public Map<String, Object> getAttributes() {
		return Map.of(
			"id", oAuthId,
			"name", name,
			"email", email,
			"profileImg", profileImg
		);
	}
}