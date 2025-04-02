package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimeSlotsReq(
        @Valid
        TimePeriodReq reservationTime,

        @NotNull(message = "인원수는 필수입니다")
        @Min(value = 1, message = "인원수는 1 보다 작을 수 없습니다")
        Integer partySize
) {
}
