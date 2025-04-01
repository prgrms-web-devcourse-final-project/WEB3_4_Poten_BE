package com.beanSpot.WEB3_4_Poten_BE.domain.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;
import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.AuthService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.MemberDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req.UpdateMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res.ResponseMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.service.MemberService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/auth/me")
@Tag(name = "Member", description = "Member Controller")
@RequiredArgsConstructor
public class MemberController {

	private final AuthService authService;
	private final MemberService memberService;

	@GetMapping
	@Operation(summary = "현재 로그인한 사용자 정보 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
		@ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
	})
	public ResponseEntity<?> getUserInfo() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof SecurityUser) {
			SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
			return ResponseEntity.ok(new MemberDto(securityUser.getMember()));
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	@GetMapping("/my")
	@Operation(summary = "사용자 마이페이지 정보 조회")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공",
			content = @Content(schema = @Schema(implementation = ResponseMemberMyPageDto.class))),
		@ApiResponse(responseCode = "400", description = "로그인이 필요합니다"),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다")
	})
	public ResponseEntity<ResponseMemberMyPageDto> getMyPage(@AuthenticationPrincipal SecurityUser securityUser) {
		if (securityUser == null) {
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "올바른 요청이 아닙니다. 로그인 상태를 확인하세요.");
		}

		Member member = memberService.getMemberById(securityUser.getMember().getId());
		ResponseMemberMyPageDto memberMyPageDto = new ResponseMemberMyPageDto(member);

		return ResponseEntity.ok(memberMyPageDto);
	}

	@PutMapping("/my")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "정보 수정 성공",
			content = @Content(schema = @Schema(implementation = ResponseMemberMyPageDto.class))),
		@ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패"),
		@ApiResponse(responseCode = "401", description = "인증 실패"),
		@ApiResponse(responseCode = "409", description = "이메일 중복")
	})
	public ResponseEntity<?> putMyPage(
		@RequestBody @Valid UpdateMemberMyPageDto updateMemberMyPageDto,
		BindingResult bindingResult,
		@AuthenticationPrincipal SecurityUser securityUser) {

		// 유효성 검사 실패 처리
		if (bindingResult.hasErrors()) {
			Map<String, String> errors = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error ->
				errors.put(error.getField(), error.getDefaultMessage())
			);
			return ResponseEntity.badRequest().body(errors);
		}

		if (securityUser == null) {
			throw new ServiceException(HttpStatus.UNAUTHORIZED.value(), "로그인이 필요합니다.");
		}

		try {
			Member member = memberService.getMemberById(securityUser.getMember().getId());

			// 회원 정보 업데이트
			memberService.updateMemberInfo(member.getOAuthId(), updateMemberMyPageDto, member.getEmail());
			Member updatedMember = memberService.getMemberById(member.getId());
			ResponseMemberMyPageDto responseMemberMyPageDto = new ResponseMemberMyPageDto(updatedMember);

			log.info("사용자 정보가 성공적으로 업데이트되었습니다. ID: {}", member.getId());
			return ResponseEntity.ok(responseMemberMyPageDto);
		} catch (ServiceException e) {
			log.error("사용자 정보 업데이트 실패: {}", e.getMessage());
			return ResponseEntity.status(e.getResultCode()).body(Map.of("message", e.getMessage()));
		} catch (Exception e) {
			log.error("사용자 정보 업데이트 중 예상치 못한 오류 발생: ", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."));
		}
	}

	@PostMapping("/logout")
	@Operation(summary = "로그아웃")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "로그아웃 성공")
	})
	public ResponseEntity<?> logout() {
		return authService.logout();
	}
}
