package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TimeSlotsReq(
        @NotNull(message = "시작 시간은 필수입니다.")
        @Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime startTime,

        @NotNull(message = "종료 시간은 필수입니다.")
        @Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
        LocalDateTime endTime,

        @NotNull(message = "인원수는 필수입니다")
        @Min(value = 1, message = "인원수는 1 보다 작을 수 없습니다")
        Integer partySize
) {
}
