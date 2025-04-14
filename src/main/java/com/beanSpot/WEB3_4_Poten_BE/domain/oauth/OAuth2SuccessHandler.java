package com.beanSpot.WEB3_4_Poten_BE.domain.oauth;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
	private final JwtService jwtService;
	private final OAuth2AuthorizedClientService authorizedClientService;

	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {
		SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
		Member member = securityUser.getMember();

		log.info("OAuth 인증 성공: 사용자 이메일 = {}, SNS = {}", member.getEmail(), member.getSnsType());

		String jwtToken = jwtService.generateToken(member);
		String refreshToken = jwtService.generateRefreshToken(member);

		log.info("JWT 토큰 생성 완료: accessToken 존재 = {}, refreshToken 존재 = {}",
			jwtToken != null, refreshToken != null);

		// 토큰 URL 인코딩
		String encodedToken = URLEncoder.encode(jwtToken, StandardCharsets.UTF_8);
		String encodedRefreshToken = URLEncoder.encode(refreshToken, StandardCharsets.UTF_8);

		// SNS 타입에 따라 다른 콜백 URL로 리다이렉트
		String snsType = member.getSnsType().toString().toLowerCase();
		String redirectUrl = String.format(
			"http://localhost:5173/auth/callback/%s?accessToken=%s&refreshToken=%s",
			snsType, encodedToken, encodedRefreshToken
		);

		log.info("리다이렉트 URL: {}", redirectUrl);

		getRedirectStrategy().sendRedirect(request, response, redirectUrl);
	}
}