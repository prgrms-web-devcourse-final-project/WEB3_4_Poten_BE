package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin Login")
public class AdminLoginController {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Operation(
		summary = "관리자 로그인",
		description = "관리자 로그인입니다.")
	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody AdminLoginDto loginDto) {
		try {
			log.debug("관리자 로그인 시도: {}", loginDto.email());

			// 관리자 계정 조회 (이메일 기준) - orElseThrow 사용
			Member admin = memberRepository.findByEmailAndMemberType(loginDto.email(), Member.MemberType.ADMIN)
				.orElseThrow(() -> {
					log.error("로그인 실패: 해당 이메일의 관리자 계정이 존재하지 않음");
					return new ServiceException(HttpStatus.UNAUTHORIZED.value(), "로그인 실패 - 계정을 찾을 수 없음");
				});

			log.debug("관리자 계정 찾음: {}, 암호화된 비밀번호 존재: {}", admin.getEmail(), admin.getPassword() != null);

			// 비밀번호 확인
			boolean isPasswordCorrect = passwordEncoder.matches(loginDto.password(), admin.getPassword());
			log.debug("비밀번호 검증 결과: {}", isPasswordCorrect);

			if (!isPasswordCorrect) {
				log.error("로그인 실패: 비밀번호 불일치");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 - 비밀번호 불일치");
			}

			// 이메일을 이용한 로그인 후 oAuthId로 JWT 발급
			String accessToken = jwtService.generateToken(admin);
			String refreshToken = jwtService.generateRefreshToken(admin);

			log.debug("토큰 생성 완료: accessToken={}, refreshToken={}", accessToken != null, refreshToken != null);

			return ResponseEntity.ok()
				.header("Authorization", "Bearer " + accessToken)
				.header("RefreshToken", refreshToken)
				.body(Map.of("message", "관리자 로그인 성공", "userId", admin.getId()));
		} catch (Exception e) {
			log.error("로그인 처리 중 예외 발생", e);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "로그인 실패: " + e.getMessage()));
		}
	}

	@Operation(
		summary = "관리자 로그아웃",
		description = "관리자 로그아웃을 처리합니다.")
	@PostMapping("/admin/logout")
	public ResponseEntity<?> logout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			jwtService.blacklistToken(token);
			return ResponseEntity.ok(Map.of("message", "로그아웃 되었습니다."));
		}
		return ResponseEntity.badRequest().body(Map.of("message", "유효한 토큰이 없습니다."));
	}
}
