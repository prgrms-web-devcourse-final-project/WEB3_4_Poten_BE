package com.beanSpot.WEB3_4_Poten_BE.domain.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req.ApplicationReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.service.ApplicationService;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;


@Tag(name = "Application", description = "Application Controller")
@RestController
@RequestMapping("/api/cafe-application")
@RequiredArgsConstructor
public class ApplicationController {
	private final ApplicationService applicationService;
	private final UserRepository userRepository;

	//인증 방식에 따라서 수정해야합니다.
	@Operation(
		summary = "신청 추가",
		description = "신청을 추가합니다. 추가 시 신청의 상태 기본값은 PENDING입니다.")
	@PostMapping
	public ResponseEntity<ApplicationRes> createApplication(@RequestBody ApplicationReq applicationReq) {
		ApplicationRes applicationRes = applicationService.createApplication(applicationReq, 1L);
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

	@Operation(
		summary = "대기중인 신청 리스트 반환",
		description = "PENDING 상태인 신청의 리스트를 반환합니다.")
	@GetMapping("/pending")
	public ResponseEntity<List<ApplicationRes>> getPendingRequests() {
		List<ApplicationRes> pendingRequests = applicationService.getPendingRequests();
		return ResponseEntity.ok(pendingRequests);
	}

	@Operation(
		summary = "신청 허용",
		description = "신청을 APPROVED 상태로 바꿉니다. 신청을 보낸 사용자의 상태가 OWNER로 변경됩니다.")
	@PostMapping("/approve/{id}")
	public ResponseEntity<ApplicationApprovedRes> approveCafe(@PathVariable Long id) {
		ApplicationApprovedRes applicationApprovedRes = applicationService.approveCafe(id);
		return ResponseEntity.ok(applicationApprovedRes);
	}

	@Operation(
		summary = "신청 거부",
		description = "신청을 REJECTED 상태로 바꿉니다.")
	@PostMapping("/reject/{id}")
	public ResponseEntity<ApplicationRes> rejectCafe(@PathVariable Long id) {
		ApplicationRes rejectedCafe = applicationService.rejectCafe(id);
		return ResponseEntity.ok(rejectedCafe);
	}
}
