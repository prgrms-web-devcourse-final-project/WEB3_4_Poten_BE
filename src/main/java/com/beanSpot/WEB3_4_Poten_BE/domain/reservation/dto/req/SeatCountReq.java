package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record SeatCountReq(
        @Valid
        TimePeriodReq reservationTime
) {

}
