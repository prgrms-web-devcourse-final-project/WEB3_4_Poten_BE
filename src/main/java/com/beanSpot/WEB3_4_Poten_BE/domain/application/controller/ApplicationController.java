package com.beanSpot.WEB3_4_Poten_BE.domain.application.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationApprovedRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.service.ApplicationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/cafe-application")
@RequiredArgsConstructor
public class ApplicationController {
	private final ApplicationService applicationService;

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
