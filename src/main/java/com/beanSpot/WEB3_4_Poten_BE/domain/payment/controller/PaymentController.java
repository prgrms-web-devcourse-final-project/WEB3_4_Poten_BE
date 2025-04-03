package com.beanSpot.WEB3_4_Poten_BE.domain.payment.controller;

import java.net.URI;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req.PaymentConfirmRequest;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.exception.PaymentException;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.res.PaymentResponse;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reservation/payment") // 예약 관련 결제 API
public class PaymentController {

    private final PaymentService paymentService;



    /**
     * 결제 성공 시 처리
     */
    @GetMapping("/success")
    public ResponseEntity<?> tossPaymentSuccess(@RequestParam String paymentKey,
        @RequestParam String orderId,
        @RequestParam long amount) {

        PaymentResponse paymentResponse = paymentService.confirmPayment(paymentKey, orderId, amount);

        // 성공 페이지로 리다이렉트
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/reservation/payment/success" +
            "?orderId=" + paymentResponse.getOrderId() +
            "&amount=" + paymentResponse.getTotalAmount() +
            "&paymentKey=" + paymentResponse.getPaymentKey()));

        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    /**
     * 결제 실패 시 처리
     */
    @GetMapping("/fail")
    public ResponseEntity<?> tossPaymentFail(@RequestParam String code,
        @RequestParam String message,
        @RequestParam(required = false) String orderId) {

        log.error("결제 실패: code={}, message={}, orderId={}", code, message, orderId);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("http://localhost:3000/reservation/payment/fail" +
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
    @PostMapping("/api/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        try {
            PaymentResponse response = paymentService.confirmPayment(
                request.getPaymentKey(),
                request.getOrderId(),
                request.getAmount()
            );
            return ResponseEntity.ok(response);
        } catch (PaymentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "message", e.getMessage(),
                "code", "RESERVATION_PAYMENT_ERROR"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "message", "결제 처리 중 오류가 발생했습니다.",
                "code", "SERVER_ERROR"
            ));
        }
    }
}
