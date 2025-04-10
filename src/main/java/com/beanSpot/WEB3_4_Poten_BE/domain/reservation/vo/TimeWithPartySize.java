package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.vo;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.common.entity.Reservable;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.redis.entity.RedisReservation;

import java.time.LocalDateTime;
import java.util.List;


public record TimeWithPartySize(
        LocalDateTime time,
        int partySize
) {
    public static void convertAndAdd(Reservable reservation, List<TimeWithPartySize> events) {

        // 시작 이벤트는 좌석 추가
        events.add(new TimeWithPartySize(reservation.getStartTime(), reservation.getPartySize()));
        // 종료 이벤트는 좌석 감소
        events.add(new TimeWithPartySize(reservation.getEndTime(), -reservation.getPartySize()));
    }

}
