package com.beanSpot.WEB3_4_Poten_BE.domain.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req.ApplicationReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.service.ApplicationService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Application", description = "Application Controller")
@RestController
@RequestMapping("/api/cafe-application")
@RequiredArgsConstructor
public class ApplicationController {
	private final ApplicationService applicationService;
	private final MemberRepository memberRepository;


	//인증 방식에 따라서 수정해야합니다.
	@Operation(
		summary = "신청 추가",
		description = "신청을 추가합니다. 추가 시 신청의 상태 기본값은 PENDING입니다.")
	@PostMapping
	public ResponseEntity<ApplicationRes> createApplication(@RequestBody ApplicationReq applicationReq,
			@AuthenticationPrincipal SecurityUser securityUser) {
		ApplicationRes applicationRes = applicationService.createApplication(applicationReq, securityUser.getId());
		return ResponseEntity.ok(applicationRes);
	}

	@Operation(
		summary = "거부된 신청 삭제",
		description = "거부된 신청을 삭제합니다. REJECTED 상태를 가진 신청만 삭제할 수 있습니다.")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRejectedApplication(@PathVariable Long id) {
		applicationService.deleteRejectedApplication(id);
		return ResponseEntity.noContent()
			.build();
	}
}