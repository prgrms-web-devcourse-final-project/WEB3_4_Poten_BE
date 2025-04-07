package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.validation.annotation.ValidTimePeriod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@ValidTimePeriod
public record SeatCountReq(
        @NotNull(message = "종료 시간은 필수입니다.")
        @Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        @Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime endTime

) implements TimePeriod
{

}
