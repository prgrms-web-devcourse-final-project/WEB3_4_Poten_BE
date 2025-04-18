package com.beanSpot.WEB3_4_Poten_BE.domain.oauth;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.service.newMemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
	private final newMemberService memberService;
	private final JwtService jwtService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(request);

		// 사용하는 SNS 타입 확인 (kakao or naver or google)
		String registrationId = request.getClientRegistration().getRegistrationId().toUpperCase();
		Member.SnsType snsType = Member.SnsType.valueOf(registrationId);

		Map<String, Object> attributes = oauth2User.getAttributes();
		String oAuthId = oauth2User.getName();
		String email = "";
		String name = "";
		String profileImg = "";

		// SNS 타입에 따라 데이터 추출 방식이 다름
		//카카오 로그인 처리
		if (snsType == Member.SnsType.KAKAO) {
			Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.getOrDefault("kakao_account",
				new HashMap<>());
			Map<String, Object> profile = (Map<String, Object>)kakaoAccount.getOrDefault("profile", new HashMap<>());
			email = (String)kakaoAccount.getOrDefault("email", "");
			name = (String)profile.getOrDefault("nickname", "");
			profileImg = (String)profile.getOrDefault("profile_image_url", "");
		}
		//Naver 로그인 처리
		else if (snsType == Member.SnsType.NAVER) {
			Map<String, Object> response = (Map<String, Object>)attributes.getOrDefault("response", new HashMap<>());
			email = (String)response.getOrDefault("email", "");
			name = (String)response.getOrDefault("name", "");
			profileImg = (String)response.getOrDefault("profile_image", "");
		}
		// Google 로그인 처리
		else if (snsType == Member.SnsType.GOOGLE) {
			email = (String)attributes.getOrDefault("email", "");
			name = (String)attributes.getOrDefault("name", "");
			profileImg = (String)attributes.getOrDefault("picture", "");
		}

		// modifyOrJoin 메서드를 사용하여 회원 정보 저장
		Member member = memberService.modifyOrJoin(oAuthId, email, name, profileImg, snsType);
		log.info("회원 정보 처리 완료: id={}, email={}", member.getId(), member.getEmail());

		// 리프레시 토큰 생성
		String refreshToken = jwtService.generateRefreshToken(member);

		return new SecurityUser(member);
	}
}