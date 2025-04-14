package com.beanSpot.WEB3_4_Poten_BE.domain.admin.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.admin.dto.res.AdminCafeListRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.admin.service.CafeAdminService;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.service.CafeService;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin Cafe Controller")
public class AdminCafeController {

	private final CafeAdminService cafeAdminService;
	private final CafeService cafeService;

	@Operation(
		summary = "대기 중인 카페 신청 목록 조회",
		description = "관리자가 승인 대기 중인 카페 신청 목록을 조회합니다.")
	@GetMapping("/applications/pending")
	public ResponseEntity<List<ApplicationRes>> getPendingApplications() {
		try {
			List<ApplicationRes> pendingApplications = cafeAdminService.getPendingApplications();
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
			ApplicationRes approvedApplication = cafeAdminService.approveApplication(applicationId);
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
			ApplicationRes rejectedApplication = cafeAdminService.rejectApplication(applicationId);
			return ResponseEntity.ok(rejectedApplication);
		} catch (Exception e) {
			log.error("신청 거절 중 오류 발생", e);
			throw new ServiceException("신청 거절 처리 실패: " + e.getMessage());
		}
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
