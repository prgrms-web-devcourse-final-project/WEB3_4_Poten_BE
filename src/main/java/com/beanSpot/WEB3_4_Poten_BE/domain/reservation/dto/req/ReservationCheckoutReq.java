package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationCheckoutReq(
        @NotNull(message = "종료 시간은 필수입니다.")
        LocalDateTime checkoutTime
) {
}
