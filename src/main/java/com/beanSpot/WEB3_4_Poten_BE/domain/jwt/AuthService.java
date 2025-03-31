package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	// 토큰 유효성 검사 - Authorization 헤더 사용
	public Member validateTokenAndGetMember(String token) {
		if (token == null) {
			throw new ServiceException(401, "로그인이 필요합니다.");
		}

		if (!jwtService.validateToken(token)) {
			throw new ServiceException(401, "Invalid token");
		}

		String oAuthId = jwtService.getOAuthIdFromToken(token);
		return memberRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new ServiceException(404, "사용자를 찾을 수 없습니다."));
	}

	// JWT 기반 로그인 처리 - 헤더 기반 응답
	public ResponseEntity<?> authenticate(String token) {
		Member member = validateTokenAndGetMember(token);

		String newAccessToken = jwtService.generateToken(member);
		String newRefreshToken = jwtService.generateRefreshToken(member);

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + newAccessToken)
			.header("RefreshToken", newRefreshToken)
			.body("Authentication successful");
	}

	// 사용자 정보 조회 - 헤더 기반 인증 적용
	public Member getMemberFromToken(String token) {
		if (token == null) {
			throw new ServiceException(401, "로그인이 필요합니다.");
		}

		if (!jwtService.validateToken(token)) {
			throw new ServiceException(401, "Invalid token");
		}

		String oAuthId = jwtService.getOAuthIdFromToken(token);
		return memberRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> new ServiceException(404, "사용자를 찾을 수 없습니다."));
	}

	// 로그아웃 - 클라이언트 측에서 토큰 삭제 권장
	public ResponseEntity<?> logout() {
		return ResponseEntity.ok()
			.body("로그아웃 되었습니다. 클라이언트에서 토큰을 삭제해주세요.");
	}

    /*
    // 쿠키 기반 로그인 처리
    public ResponseEntity<?> authenticateWithCookies(String token) {
        Member member = validateTokenAndGetMember(token);

        String newAccessToken = jwtService.generateToken(member);
        String newRefreshToken = jwtService.generateRefreshToken(member);

        // 쿠키에 accessToken, refreshToken 저장
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60) // 1시간
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("None")
                .path("/api/auth/refresh")
                .maxAge(7 * 24 * 60 * 60) // 7일
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", accessTokenCookie.toString())
                .header("Set-Cookie", refreshTokenCookie.toString())
                .body("Authentication successful");
    }

    // 쿠키 기반 로그아웃
    public ResponseEntity<?> logoutWithCookies() {
        ResponseCookie deleteAccessToken = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .build();

        ResponseCookie deleteRefreshToken = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0)
                .secure(true)
                .build();

        return ResponseEntity.ok()
                .header("Set-Cookie", deleteAccessToken.toString())
                .header("Set-Cookie", deleteRefreshToken.toString())
                .body("로그아웃 되었습니다.");
    }
    */
}