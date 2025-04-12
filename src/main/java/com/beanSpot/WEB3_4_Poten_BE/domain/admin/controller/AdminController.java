package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.AdminLoginDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.res.AdminCafeListRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.AdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service.CafeService;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res.MemberResponseDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

;

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
	private final CafeService cafeService;

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
		summary = "대기 중인 카페 신청 목록 조회",
		description = "관리자가 승인 대기 중인 카페 신청 목록을 조회합니다.")
	@GetMapping("/applications/pending")
	public ResponseEntity<List<ApplicationRes>> getPendingApplications() {
		try {
			List<ApplicationRes> pendingApplications = adminService.getPendingApplications();
			return ResponseEntity.ok(pendingApplications);
		} catch (Exception e) {
			log.error("대기 중인 신청 목록 조회 중 오류 발생", e);
			throw new ServiceException("대기 중인 신청 목록 조회 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "카페 신청 승인",
		description = "관리자가 카페 신청을 승인합니다. 승인 시 신청자의 권한이 OWNER로 변경됩니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "신청 승인 성공"),
		@ApiResponse(responseCode = "400", description = "이미 처리된 신청이거나 유효하지 않은 신청"),
		@ApiResponse(responseCode = "404", description = "신청을 찾을 수 없음")
	})
	@PostMapping("/applications/{applicationId}/approve")
	public ResponseEntity<ApplicationRes> approveApplication(@PathVariable Long applicationId) {
		try {
			ApplicationRes approvedApplication = adminService.approveApplication(applicationId);
			return ResponseEntity.ok(approvedApplication);
		} catch (ServiceException e) {
			log.error("신청 승인 중 오류 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("신청 승인 중 예상치 못한 오류 발생", e);
			throw new ServiceException("신청 승인 처리 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "카페 신청 거절",
		description = "관리자가 카페 신청을 거절합니다.")
	@PostMapping("/applications/{applicationId}/reject")
	public ResponseEntity<ApplicationRes> rejectApplication(@PathVariable Long applicationId) {
		try {
			ApplicationRes rejectedApplication = adminService.rejectApplication(applicationId);
			return ResponseEntity.ok(rejectedApplication);
		} catch (Exception e) {
			log.error("신청 거절 중 오류 발생", e);
			throw new ServiceException("신청 거절 처리 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "회원 목록 조회",
		description = "관리자가 회원 목록을 조회합니다. 선택적으로 회원 유형으로 필터링할 수 있습니다.")
	@GetMapping("/members")
	public ResponseEntity<List<MemberResponseDto>> getMembers(
		@RequestParam(required = false) Member.MemberType memberType) {
		try {
			// 회원 목록 조회 및 DTO 변환
			List<Member> members = (memberType != null)
				? adminService.getMembersByType(memberType)
				: adminService.getAllMembers();

			List<MemberResponseDto> memberDtos = members.stream()
				.map(MemberResponseDto::fromEntity)
				.collect(Collectors.toList());

			return ResponseEntity.ok(memberDtos);
		} catch (Exception e) {
			log.error("회원 목록 조회 중 오류 발생", e);
			throw new ServiceException("회원 목록 조회 실패: " + e.getMessage());
		}
	}

	@Operation(
		summary = "회원 상세 정보 조회",
		description = "관리자가 특정 회원의 상세 정보를 조회합니다.")
	@GetMapping("/members/{memberId}")
	public ResponseEntity<MemberResponseDto> getMemberDetails(@PathVariable Long memberId) {
		try {
			Member member = adminService.getMemberById(memberId);
			MemberResponseDto memberDto = MemberResponseDto.fromEntity(member);
			return ResponseEntity.ok(memberDto);
		} catch (ServiceException e) {
			log.error("회원 상세 정보 조회 중 오류 발생: {}", e.getMessage());
			throw e;
		} catch (Exception e) {
			log.error("회원 상세 정보 조회 중 예상치 못한 오류 발생", e);
			throw new ServiceException("회원 상세 정보 조회 실패: " + e.getMessage());
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

	@Operation(
		summary = "관리자용 카페 리스트 조회",
		description = "관리자가 카페 목록을 관리하기 위한 간소화된 정보를 페이징하여 제공합니다.")
	@GetMapping("/admin/cafes")
	public ResponseEntity<Page<AdminCafeListRes>> getAdminCafeList(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		Page<AdminCafeListRes> cafes = cafeService.getAdminCafeList(page, size);
		return ResponseEntity.ok(cafes);
	}
}