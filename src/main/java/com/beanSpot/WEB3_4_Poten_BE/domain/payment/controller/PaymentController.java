package com.beanSpot.WEB3_4_Poten_BE.domain.payment.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req.PaymentCancelReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req.PaymentConfirmReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.res.PaymentRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.exception.PaymentException;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@Tag(name = "Payment", description = "Payment Controller")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservation/payment") // 예약 관련 결제 API
public class PaymentController {

	private final PaymentService paymentService;
	private static final String FRONTEND_URL = "http://localhost:3000/reservation/payment";

	/**
	 * 결제 성공 시 처리
	 */
	@Operation(
			summary = "결제 성공",
			description = "결제 성공")
	@GetMapping("/success")
	public ResponseEntity<?> tossPaymentSuccess(
		@RequestParam String paymentKey,
		@RequestParam String orderId,
		@RequestParam long amount) {

		PaymentRes paymentRes = paymentService.confirmPayment(paymentKey, orderId, amount);

		// 성공 페이지로 리다이렉트
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(FRONTEND_URL + "/success" +
			"?orderId=" + paymentRes.getOrderId() +
			"&amount=" + paymentRes.getTotalAmount() +
			"&paymentKey=" + paymentRes.getPaymentKey()));

		return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}

	/**
	 * 결제 실패 시 처리
	 */
	@Operation(
			summary = "결제 실패",
			description = "결제 실패")
	@GetMapping("/fail")
	public ResponseEntity<?> tossPaymentFail(
		@RequestParam String code,
		@RequestParam String message,
		@RequestParam(required = false) String orderId) {

		log.error("결제 실패: code={}, message={}, orderId={}", code, message, orderId);

		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(URI.create(FRONTEND_URL + "/fail" +
			"?code=" + code +
			"&message=" + message +
			"&orderId=" + orderId));

		return ResponseEntity.badRequest().body(Map.of(
			"message", message,
			"code", "RESERVATION_PAYMENT_ERROR"
		));
	}

	/**
	 * 프론트에서 결제 확인 요청
	 */
	@Operation(
			summary = "결제 확인 요청",
			description = "결제 확인 요청")
	@PostMapping("/confirm")
	public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmReq request) {
		try {
			PaymentRes response = paymentService.confirmPayment(
				request.paymentKey(),
				request.orderId(),
				request.amount()
			);
			return ResponseEntity.ok(response);
		} catch (PaymentException e) {
			return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "RESERVATION_PAYMENT_ERROR");
		} catch (Exception e) {
			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "결제 처리 중 오류가 발생했습니다.", "SERVER_ERROR");
		}
	}

	private ResponseEntity<Map<String, String>> createErrorResponse(HttpStatus status, String message, String code) {
		return ResponseEntity.status(status).body(Map.of(
			"message", message,
			"code", code
		));
	}

	/**
	 * 결제 취소 요청 처리 컨트롤러
	 *
	 * @author -- 김남우 --
	 * @since -- 4월 5일 --
	 */
	@Operation(
			summary = "결제 취소",
			description = "결제 취소")
	@PostMapping("/cancel")
	public ResponseEntity<?> cancelPayment(@RequestBody PaymentCancelReq request) {
		try {
			PaymentRes response = paymentService.cancelPayment(
					request.paymentKey(),
					request.cancelAmount(),
					request.cancelReason()
			);
			return ResponseEntity.ok(response);
		} catch (PaymentException e) {
			return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), "RESERVATION_CANCEL_ERROR");
		} catch (Exception e) {
			return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "결제 취소 중 오류가 발생했습니다.", "SERVER_ERROR");
		}
	}
}