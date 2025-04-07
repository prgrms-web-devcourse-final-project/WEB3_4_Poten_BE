package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.service.newMemberService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@RequiredArgsConstructor
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	private final newMemberService memberService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
		throws ServletException, IOException {
		try {
			log.info("현재 요청 URI: {}", request.getRequestURI());

			// 토큰 추출
			String token = extractTokenFromRequest(request);

			// 토큰이 존재하면 인증 처리
			if (token != null) {
				processAuthentication(token);
			}
		} catch (Exception e) {
			log.error("토큰 처리 중 오류 발생: ", e);
			// 예외가 발생해도 필터 체인은 계속 진행
		}

		chain.doFilter(request, response);
	}

	/**
	 * 요청에서 토큰을 추출합니다.
	 */
	private String extractTokenFromRequest(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return authHeader.substring(7); // "Bearer " 이후의 토큰 부분 추출
		}

		return null;
	}

	/**
	 * 토큰 검증 및 인증 처리를 수행합니다.
	 */
	private void processAuthentication(String token) {
		// 토큰 검증
		if (!jwtService.validateToken(token)) {
			log.warn("유효하지 않은 토큰입니다.");
			return;
		}

		try {
			// 토큰에서 사용자 ID 추출
			String oAuthId = jwtService.getOAuthIdFromToken(token);
			log.info("토큰에서 추출된 oAuthId: {}", oAuthId);

			// 사용자 정보 조회
			Member member = memberService.findByOAuthId(oAuthId)
				.orElseThrow(() -> new UsernameNotFoundException("ID: " + oAuthId + "에 해당하는 사용자를 찾을 수 없습니다."));

			log.info("회원 인증 성공: {}", member.getEmail());

			// 인증 객체 생성 및 SecurityContext에 설정
			setupAuthentication(member);
		} catch (UsernameNotFoundException e) {
			log.error("사용자 조회 실패: {}", e.getMessage());
		}
	}

	/**
	 * 인증 객체를 생성하고 SecurityContext에 설정합니다.
	 */
	private void setupAuthentication(Member member) {
		SecurityUser securityUser = new SecurityUser(member);
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			securityUser, null, securityUser.getAuthorities());

		SecurityContextHolder.getContext().setAuthentication(authentication);
		log.debug("SecurityContext에 인증 정보 설정 완료");
	}
}