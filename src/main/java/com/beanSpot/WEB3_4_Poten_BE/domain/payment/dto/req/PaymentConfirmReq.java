package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record PaymentConfirmReq(

	@NotEmpty
	String paymentKey,

	@NotEmpty
	String orderId,

	@NotNull
	Long amount
) {
}