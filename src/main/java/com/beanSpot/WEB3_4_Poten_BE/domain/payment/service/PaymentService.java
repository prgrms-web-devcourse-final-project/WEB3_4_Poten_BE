package com.beanSpot.WEB3_4_Poten_BE.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity.Payment;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.exception.PaymentException;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.repository.PaymentRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.res.PaymentResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final RestTemplate restTemplate = new RestTemplate();

	@Value("${payment.toss.secret-key}")
	private String secretKey;

	@Value("${payment.toss.confirm-url}")
	private String confirmUrl;

	@Transactional
	public PaymentResponse confirmPayment(String paymentKey, String orderId, Long amount) {
		try {
			log.info("토스페이먼츠 결제 승인 요청: paymentKey={}, orderId={}, amount={}", paymentKey, orderId, amount);

			// 인증 정보 생성 (Base64 인코딩)
			String encodedSecretKey = Base64.getEncoder().encodeToString(
				(secretKey + ":").getBytes(StandardCharsets.UTF_8)
			);

			// HTTP 헤더 설정
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.set("Authorization", "Basic " + encodedSecretKey);

			// 요청 본문 생성
			TossPaymentRequest requestBody = new TossPaymentRequest(paymentKey, orderId, amount);
			HttpEntity<TossPaymentRequest> request = new HttpEntity<>(requestBody, headers);

			// 실제 결제 승인 요청 보내기
			PaymentResponse response = restTemplate.postForObject(confirmUrl, request, PaymentResponse.class);

			if (response == null) {
				throw new PaymentException("결제 승인 응답이 null입니다.");
			}

			processSuccessfulPayment(response);
			return response;
		} catch (RestClientException e) {
			log.error("토스페이먼츠 결제 승인 중 오류 발생: {}", e.getMessage(), e);
			throw new PaymentException("결제 승인 중 오류가 발생했습니다.", e);
		} catch (Exception e) {
			log.error("결제 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
			throw new PaymentException("결제 처리 중 오류가 발생했습니다.", e);
		}
	}

	private void processSuccessfulPayment(PaymentResponse response) {
		log.info("결제 성공: orderId={}, amount={}", response.getOrderId(), response.getTotalAmount());

		OffsetDateTime offsetDateTime = OffsetDateTime.parse(response.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		paymentRepository.save(Payment.builder()
			.tossOrderId(response.getOrderId())
			.paymentKey(response.getPaymentKey())
			.paidAmount(response.getTotalAmount())
			.paySuccessDate(offsetDateTime.toLocalDateTime())
			.method(response.getMethod())
			.build());
	}

	public record TossPaymentRequest(String paymentKey, String orderId, Long amount) {
	}
}
