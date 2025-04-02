package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.AdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin Controller")
public class AdminController {

	private final JwtService jwtService;
	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final AdminService adminService;

	@Operation(
		summary = "관리자 로그인",
		description = "관리자 로그인입니다.")
	@PostMapping("/login")
	public ResponseEntity<?> adminLogin(@RequestBody AdminLoginDto loginDto) {
		try {
			log.debug("관리자 로그인 시도: {}", loginDto.email());

			// 관리자 계정 조회 (이메일 기준)
			Optional<Member> optionalMember = memberRepository.findByEmailAndMemberType(loginDto.email(),
				Member.MemberType.ADMIN);

			if (optionalMember.isEmpty()) {
				log.error("로그인 실패: 해당 이메일의 관리자 계정이 존재하지 않음");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패 - 계정을 찾을 수 없음");
			}

			Member admin = optionalMember.get();
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
