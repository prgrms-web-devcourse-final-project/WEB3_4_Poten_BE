package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

//카페 좌석 조회 요청 dto
@Getter
public class CafeSeatsReq {
    @NotNull(message = "카페 ID는 필수입니다.")
    private long cafeId;

    @NotNull(message = "시작 시간은 필수입니다.")
    private LocalDateTime startTime;

    @Max(value = 2, message = "최대 2시간까지 예약 가능합니다.")
    @NotNull(message = "시간은 필수입니다.")
    private int durationHours;
}
