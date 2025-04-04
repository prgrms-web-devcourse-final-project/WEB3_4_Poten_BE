package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req;

public record TossPaymentReq(
	String paymentKey,
	String orderId,
	Long amount
) {
}
