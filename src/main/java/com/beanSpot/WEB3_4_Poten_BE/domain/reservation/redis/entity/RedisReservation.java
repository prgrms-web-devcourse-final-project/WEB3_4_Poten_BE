package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.redis.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.common.entity.Reservable;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class RedisReservation implements Reservable {
    private Long Id;
    private int partySize;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
