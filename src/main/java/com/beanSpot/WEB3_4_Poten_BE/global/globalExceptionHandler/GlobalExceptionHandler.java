package com.beanSpot.WEB3_4_Poten_BE.global.globalExceptionHandler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice  // @ControllerAdvice + @ResponseBody
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

	// 일반적인 리소스를 찾을 수 없는 경우
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Map<String, String>> handleNoSuchElementException(NoSuchElementException ex) {
		log.error("리소스를 찾을 수 없음: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", "요청한 리소스를 찾을 수 없습니다.");
		errorResponse.put("code", "RESOURCE_NOT_FOUND");

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	// 입력값 유효성 검사 실패
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.error("입력값 유효성 검사 실패: {}", ex.getMessage(), ex);

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("message", "입력값 유효성 검사에 실패했습니다.");
		errorResponse.put("code", "VALIDATION_ERROR");

		// 유효성 검사 실패 세부 정보 추가
		Map<String, String> validationErrors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->
			validationErrors.put(error.getField(), error.getDefaultMessage())
		);
		errorResponse.put("errors", validationErrors);

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	// 권한 부족 예외
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex) {
		log.error("접근 권한 부족: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", "해당 리소스에 대한 접근 권한이 없습니다.");
		errorResponse.put("code", "ACCESS_DENIED");

		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
	}

	// 비즈니스 로직 예외
	@ExceptionHandler(ServiceException.class)
	public ResponseEntity<Map<String, String>> handleServiceException(ServiceException ex) {
		log.error("서비스 예외 발생: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", ex.getMessage());
		errorResponse.put("code", "SERVICE_ERROR");

		return ResponseEntity.status(ex.getResultCode()).body(errorResponse);
	}

	// 존재하지 않는 값 예외 처리
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		log.error("잘못된 요청 파라미터: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", ex.getMessage());
		errorResponse.put("code", "INVALID_ARGUMENT");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	// 잘못된 상태 예외 처리
	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
		log.error("잘못된 상태 오류: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", ex.getMessage());
		errorResponse.put("code", "INVALID_STATE");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}


	// 그 외 모든 예외를 처리
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, String>> handleException(Exception ex) {
		log.error("예상치 못한 예외 발생: {}", ex.getMessage(), ex);

		Map<String, String> errorResponse = new HashMap<>();
		errorResponse.put("message", "서버 내부 오류가 발생했습니다.");
		errorResponse.put("code", "INTERNAL_SERVER_ERROR");

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
}