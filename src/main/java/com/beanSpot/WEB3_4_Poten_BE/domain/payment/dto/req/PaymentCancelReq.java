package com.beanSpot.WEB3_4_Poten_BE.domain.payment.dto.req;

import jakarta.validation.constraints.NotEmpty;

/**
 * 결제 취소 요청 DTO
 *
 * @author -- 김남우 --
 * @since -- 4월 7일 --
 */
public record PaymentCancelReq(

        @NotEmpty
        String paymentKey,

        @NotEmpty
        Long cancelAmount,

        @NotEmpty
        String cancelReason
) {}