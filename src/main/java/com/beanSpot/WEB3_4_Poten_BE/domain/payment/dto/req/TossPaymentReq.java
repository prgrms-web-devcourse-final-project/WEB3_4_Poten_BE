package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req;

import jakarta.validation.constraints.NotEmpty;

public record TossPaymentReq(

	@NotEmpty
	String paymentKey,

	@NotEmpty
	String orderId,

	@NotEmpty
	Long amount
) {
}
