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
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cafe-application")
@RequiredArgsConstructor
public class ApplicationController {
	private final ApplicationService applicationService;
	private final UserRepository userRepository;

	//인증 방식에 따라서 수정해야합니다.
	@PostMapping
	public ResponseEntity<ApplicationRes> createApplication(@RequestBody ApplicationReq applicationReq) {
		ApplicationRes applicationRes = applicationService.createApplication(applicationReq, 1L);
		return ResponseEntity.ok(applicationRes);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteRejectedApplication(@PathVariable Long id) {
		applicationService.deleteRejectedApplication(id);
		return ResponseEntity.noContent()
			.build();
	}

	@GetMapping("/pending")
	public ResponseEntity<List<ApplicationRes>> getPendingRequests() {
		List<ApplicationRes> pendingRequests = applicationService.getPendingRequests();
		return ResponseEntity.ok(pendingRequests);
	}

	@PostMapping("/approve/{id}")
	public ResponseEntity<ApplicationApprovedRes> approveCafe(@PathVariable Long id) {
		ApplicationApprovedRes applicationApprovedRes = applicationService.approveCafe(id);
		return ResponseEntity.ok(applicationApprovedRes);
	}

	@PostMapping("/reject/{id}")
	public ResponseEntity<ApplicationRes> rejectCafe(@PathVariable Long id) {
		ApplicationRes rejectedCafe = applicationService.rejectCafe(id);
		return ResponseEntity.ok(rejectedCafe);
	}
}
