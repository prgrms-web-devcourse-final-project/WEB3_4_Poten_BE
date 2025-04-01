package com.beanSpot.WEB3_4_Poten_BE.domain.oauth;

import java.util.Map;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
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

	public static OAuthAttributes oAuthAttributes(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
		if ("kakao".equals(registrationId)) {
			return oAuthAttributesKakao(userNameAttributeName, attributes);
		} else if ("naver".equals(registrationId)) {
			return oAuthAttributesNaver(userNameAttributeName, attributes);
		} else if ("google".equals(registrationId)) {
			return oAuthAttributesGoogle(userNameAttributeName, attributes);
		}

		throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + registrationId);
	}

	private static OAuthAttributes oAuthAttributesKakao(String userNameAttributeName, Map<String, Object> attributes) {
		// 카카오 로그인 처리 로직
		Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
		Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

		log.info("OAuthAttributes - Kakao login");
		log.info("OAuthAttributes - kakaoAccount: {}", kakaoAccount);
		log.info("OAuthAttributes - profile: {}", profile);
		log.info("OAuthAttributes - nickname: {}", profile.get("nickname"));
		log.info("OAuthAttributes - email: {}", kakaoAccount.get("email"));

		return OAuthAttributes.builder()
			.name((String) profile.getOrDefault("nickname", "Unknown"))
			.email((String) kakaoAccount.getOrDefault("email", "no-email@example.com"))
			.oAuthId(String.valueOf(attributes.getOrDefault("id", "")))
			.attributes(attributes)
			.profileImg((String) profile.getOrDefault("profile_image_url", ""))
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	private static OAuthAttributes oAuthAttributesNaver(String userNameAttributeName, Map<String, Object> attributes) {
		// 네이버 로그인 처리 로직
		Map<String, Object> response = (Map<String, Object>) attributes.get("response");

		log.info("OAuthAttributes - naverResponse: {}", response);
		log.info("OAuthAttributes - name: {}", response.get("name"));
		log.info("OAuthAttributes - email: {}", response.get("email"));

		return OAuthAttributes.builder()
			.name((String) response.getOrDefault("name", "Unknown"))
			.email((String) response.getOrDefault("email", "no-email@example.com"))
			.oAuthId(String.valueOf(response.get("id")))
			.attributes(attributes)
			.profileImg((String) response.getOrDefault("profile_image", ""))
			.nameAttributeKey(userNameAttributeName)
			.build();
	}

	private static OAuthAttributes oAuthAttributesGoogle(String userNameAttributeName, Map<String, Object> attributes) {
		// 구글 로그인 처리 로직
		log.info("OAuthAttributes - Google login");
		log.info("OAuthAttributes - name: {}", attributes.get("name"));
		log.info("OAuthAttributes - email: {}", attributes.get("email"));

		return OAuthAttributes.builder()
			.name((String) attributes.get("name"))
			.email((String) attributes.get("email"))
			.oAuthId((String) attributes.get("sub"))
			.attributes(attributes)
			.profileImg((String) attributes.getOrDefault("picture", ""))
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