package com.beanSpot.WEB3_4_Poten_BE.domain.member.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.MemberDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req.UpdateMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res.ResponseMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.service.newMemberService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

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
@RequestMapping("/mypage")
@Tag(name = "Member", description = "Member Controller")
@RequiredArgsConstructor
public class MemberController {

	private final newMemberService memberService;

	@GetMapping("/myinfo")
	@Operation(summary = "내 정보 조회")
	public ResponseEntity<?> getUserInfo(@AuthenticationPrincipal SecurityUser securityUser) {
		// 인증된 사용자 정보를 바로 매개변수로 받음
		return ResponseEntity.ok(MemberDto.from(securityUser.getMember()));
	}

	@PutMapping("/myinfo")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "정보 수정 성공",
			content = @Content(schema = @Schema(implementation = ResponseMemberMyPageDto.class))),
		@ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패")
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

		try {
			Member member = memberService.getMemberById(securityUser.getMember().getId());

			// 회원 정보 업데이트
			memberService.updateMemberInfo(member.getOAuthId(), updateMemberMyPageDto, member.getEmail());
			Member updatedMember = memberService.getMemberById(member.getId());
			ResponseMemberMyPageDto responseMemberMyPageDto = ResponseMemberMyPageDto.from(updatedMember);

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
}
