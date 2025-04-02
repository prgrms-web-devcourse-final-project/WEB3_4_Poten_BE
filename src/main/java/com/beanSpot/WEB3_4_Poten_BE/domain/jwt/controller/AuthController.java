package com.beanSpot.WEB3_4_Poten_BE.domain.jwt.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "인증 관련 API")
public class AuthController {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final AuthService authService;

	@PostMapping("/refresh")
	@Operation(
		summary = "액세스 토큰 갱신",
		description = "리프레시 토큰을 사용해 새로운 액세스 토큰을 발급합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 갱신 성공"),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 리프레시 토큰"),
		@ApiResponse(responseCode = "400", description = "사용자를 찾을 수 없음")
	})
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
				.orElseThrow(() -> new ServiceException(400, "사용자를 찾을 수 없습니다."));

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

	@PostMapping("/logout")
	@Operation(
		summary = "로그아웃",
		description = "사용자의 토큰을 무효화하여 로그아웃 처리합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 성공")
	})
	public ResponseEntity<?> logout(
		@RequestHeader(value = "Authorization", required = false) String authHeader) {

		return authService.logout(authHeader);
	}

	@GetMapping("/validate")
	@Operation(
		summary = "토큰 유효성 검사",
		description = "액세스 토큰의 유효성을 검증합니다."
	)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 유효함"),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰")
	})
	public ResponseEntity<?> validateToken(
		@RequestHeader(value = "Authorization", required = false) String authHeader) {

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new ServiceException(401, "올바른 토큰 형식이 아닙니다.");
		}

		String token = authHeader.substring(7);

		try {
			Member member = authService.validateTokenAndGetMember(token);

			return ResponseEntity.ok(Map.of(
				"valid", true,
				"userId", member.getId(),
				"role", member.getMemberType().name()
			));
		} catch (ServiceException e) {
			return ResponseEntity.status(e.getResultCode()).body(Map.of(
				"valid", false,
				"message", e.getMessage()
			));
		}
	}
}