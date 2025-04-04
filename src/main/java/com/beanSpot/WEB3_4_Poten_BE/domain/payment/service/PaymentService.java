package com.beanSpot.WEB3_4_Poten_BE.domain.payment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req.TossPaymentReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity.Payment;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.exception.PaymentException;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.repository.PaymentRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.res.PaymentRes;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

	private final PaymentRepository paymentRepository;

	@Value("${payment.toss.secret-key}")
	private String secretKey;
	@Value("${payment.toss.confirm-url}")
	private String confirmUrl;

	private final RestClient restClient = RestClient.create();

	@Transactional
	public PaymentRes confirmPayment(String paymentKey, String orderId, Long amount) {
		try {
			PaymentRes response = restClient.post()
				.uri(confirmUrl)
				.headers(this::setAuthorizationHeader)
				.body(new TossPaymentReq(paymentKey, orderId, amount))
				.retrieve()
				.body(PaymentRes.class);

			processSuccessfulPayment(response);
			return response;
		} catch (RestClientException e) {
			log.error("토스페이먼츠 결제 승인 중 오류 발생: {}", e.getMessage(), e);
			throw new PaymentException("결제 승인 중 오류가 발생했습니다.", e);
		}
	}

	private void processSuccessfulPayment(PaymentRes response) {
		log.info("결제 성공: orderId={}, amount={}", response.getOrderId(), response.getTotalAmount());
		log.info("response: {}", response);

		OffsetDateTime offsetDateTime = OffsetDateTime.parse(response.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME);

		paymentRepository.save(Payment.builder()
			.orderId(response.getOrderId())
			.paymentKey(response.getPaymentKey())
			.amount(response.getTotalAmount())
			.paySuccessDate(offsetDateTime.toLocalDateTime())
			.method(response.getMethod())
			.build());
	}

	private void setAuthorizationHeader(HttpHeaders headers) {
		String encodedSecretKey = Base64.getEncoder()
			.encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));

		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encodedSecretKey);
	}
}

// 추후 예약(주문)과 연결할 때 추가할 로직
// 예약 시스템과 연결 후, 결제 성공 시 예약 상태를 변경하는 로직 추가 예정
