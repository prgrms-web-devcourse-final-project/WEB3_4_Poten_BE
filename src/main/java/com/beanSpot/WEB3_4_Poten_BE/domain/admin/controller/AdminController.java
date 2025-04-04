package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.AdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

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
	private final AdminService adminService;

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

	@PostMapping("/logout")
	public ResponseEntity<?> adminLogout(@RequestHeader(value = "Authorization", required = false) String authHeader) {
		try {
			// Bearer 토큰 추출
			if (authHeader != null && authHeader.startsWith("Bearer ")) {
				String token = authHeader.substring(7);

				// 토큰 블랙리스트에 추가 (JwtService에 추가 필요)
				// jwtService.addToBlacklist(token);

				// 쿠키 방식 사용 시 아래 코드 활성화
            /*
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
                    .body("관리자 로그아웃 성공");
            */
			}

			return ResponseEntity.ok()
				.body("관리자 로그아웃 성공. 클라이언트에서 토큰을 삭제해주세요.");
		} catch (Exception e) {
			log.error("로그아웃 처리 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("로그아웃 처리 중 오류가 발생했습니다.");
		}
	}

	@GetMapping("/applications/pending")
	public ResponseEntity<List<ApplicationRes>> getPendingApplications() {
		try {
			List<ApplicationRes> pendingApplications = adminService.getPendingApplications();
			return ResponseEntity.ok(pendingApplications);
		} catch (Exception e) {
			log.error("대기 중인 신청 목록 조회 중 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/applications/{id}/approve")
	public ResponseEntity<ApplicationApprovedRes> approveApplication(@PathVariable Long id) {
		try {
			ApplicationApprovedRes result = adminService.approveApplication(id);
			return ResponseEntity.ok(result);
		} catch (ServiceException e) {
			log.error("신청 승인 중 오류 발생: {}", e.getMessage());
			return ResponseEntity.status(e.getResultCode()).build();
		} catch (Exception e) {
			log.error("신청 승인 중 예상치 못한 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/applications/{id}/reject")
	public ResponseEntity<ApplicationRes> rejectApplication(@PathVariable Long id) {
		try {
			ApplicationRes result = adminService.rejectApplication(id);
			return ResponseEntity.ok(result);
		} catch (ServiceException e) {
			log.error("신청 거절 중 오류 발생: {}", e.getMessage());
			return ResponseEntity.status(e.getResultCode()).build();
		} catch (Exception e) {
			log.error("신청 거절 중 예상치 못한 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

	}
}
