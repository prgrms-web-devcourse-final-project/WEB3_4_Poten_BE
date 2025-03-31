package com.beanSpot.WEB3_4_Poten_BE.domain.jwt.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	@PostMapping("/refresh")
	public ResponseEntity<?> refreshAccessToken(
		@RequestHeader(value = "RefreshToken", required = false) String refreshToken) {

		if (refreshToken == null) {
			throw new ServiceException(401, "리프레시 토큰이 없습니다.");
		}

		// 리프레시 토큰 검증
		if (!jwtService.validateToken(refreshToken)) {
			throw new ServiceException(401, "유효하지 않은 리프레시 토큰입니다.");
		}

		try {
			String oAuthId = jwtService.getOAuthIdFromToken(refreshToken);
			Member member = memberRepository.findByoAuthId(oAuthId)
				.orElseThrow(() -> new ServiceException(404, "사용자를 찾을 수 없습니다."));

			// 새로운 액세스 토큰 생성
			String newAccessToken = jwtService.generateToken(member);

			// 헤더에 새 액세스 토큰 설정
			return ResponseEntity.ok()
				.header("Authorization", "Bearer " + newAccessToken)
				.body("액세스 토큰이 갱신되었습니다.");
		} catch (Exception e) {
			throw new ServiceException(401, "토큰 갱신에 실패했습니다: " + e.getMessage());
		}
	}

    /*
    // 쿠키 기반 토큰 갱신 - 필요할 경우 사용
    @PostMapping("/refresh-with-cookie")
    public ResponseEntity<?> refreshAccessTokenWithCookie(
            @CookieValue(value = "refreshToken", required = false) String refreshToken) {

        if (refreshToken == null) {
            throw new ServiceException(401, "리프레시 토큰이 없습니다.");
        }

        // 리프레시 토큰 검증
        if (!jwtService.validateToken(refreshToken)) {
            throw new ServiceException(401, "유효하지 않은 리프레시 토큰입니다.");
        }

        try {
            String oAuthId = jwtService.getOAuthIdFromToken(refreshToken);
            Member member = memberRepository.findByOAuthId(oAuthId)
                    .orElseThrow(() -> new ServiceException(404, "사용자를 찾을 수 없습니다."));

            // 새로운 액세스 토큰 생성
            String newAccessToken = jwtService.generateToken(member);

            // 쿠키 설정
            ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                    .path("/")
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("None")  // 개발 환경에 맞춤
                    .maxAge(60 * 60)  // 1시간
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie", accessTokenCookie.toString())
                    .body("액세스 토큰이 갱신되었습니다.");
        } catch (Exception e) {
            throw new ServiceException(401, "토큰 갱신에 실패했습니다.");
        }
    }
    */
}
