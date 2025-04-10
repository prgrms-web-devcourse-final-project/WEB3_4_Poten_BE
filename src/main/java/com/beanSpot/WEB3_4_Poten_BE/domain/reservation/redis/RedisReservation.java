package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.redis;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RedisReservation {
    private Long memberId;
    private int partySize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
