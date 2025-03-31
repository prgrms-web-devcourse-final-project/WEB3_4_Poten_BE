package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody AdminLoginDto loginDto) {
		try {
			// 관리자 계정 조회 (이메일 기준)
			Optional<Member> optionalMember = memberRepository.findByEmailAndMemberType(loginDto.email(),
				Member.MemberType.ADMIN);
			if (optionalMember.isEmpty()) {
				log.error("로그인 실패: 해당 이메일의 관리자 계정이 존재하지 않음");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 - 계정을 찾을 수 없음");
			}
			Member admin = optionalMember.get();

			// 비밀번호 확인
			boolean isPasswordCorrect = passwordEncoder.matches(loginDto.password(), admin.getPassword());

			if (!isPasswordCorrect) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 - 비밀번호 불일치");
			}

			// 이메일을 이용한 로그인 후 oAuthId로 JWT 발급
			String accessToken = jwtService.generateToken(admin);
			String refreshToken = jwtService.generateRefreshToken(admin);

			/*
			ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
					.httpOnly(true)
					.secure(true)
					.sameSite("None")
					.path("/")
					.maxAge(60 * 60) // 1시간
					.build();

			ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
					.httpOnly(true)
					.secure(true)
					.sameSite("None")
					.path("/api/auth/refresh")
					.maxAge(7 * 24 * 60 * 60) // 7일
					.build();

			return ResponseEntity.ok()
					.header("Set-Cookie", accessTokenCookie.toString())
					.header("Set-Cookie", refreshTokenCookie.toString())
					.body("관리자 로그인 성공");
			*/

			return ResponseEntity.ok()
				.header("Authorization", "Bearer " + accessToken)
				.header("RefreshToken", refreshToken)
				.body("관리자 로그인 성공");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
		}
	}
}
