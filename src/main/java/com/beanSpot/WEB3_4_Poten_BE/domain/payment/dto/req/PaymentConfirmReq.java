package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentConfirmReq {
    private String paymentKey;
    private String orderId;
    private Long amount;
}