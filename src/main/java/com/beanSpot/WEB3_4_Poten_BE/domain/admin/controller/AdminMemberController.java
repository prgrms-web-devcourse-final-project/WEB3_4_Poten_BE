package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.MemberAdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.res.MemberResponseDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin-Member", description = "회원 관리 API")
public class AdminMemberController {

	private final MemberAdminService memberAdminService;

	@Operation(
		summary = "회원 목록 조회",
		description = "관리자가 회원 목록을 조회합니다. 선택적으로 회원 유형으로 필터링할 수 있습니다.")
	@GetMapping("/members")
	public ResponseEntity<List<MemberResponseDto>> getMembers(
		@RequestParam(required = false) Member.MemberType memberType) {
		try {
			// 회원 목록 조회 및 DTO 변환
			List<Member> members = (memberType != null)
				? memberAdminService.getMembersByType(memberType)
				: memberAdminService.getAllMembers();

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
			Member member = memberAdminService.getMemberById(memberId);
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

}