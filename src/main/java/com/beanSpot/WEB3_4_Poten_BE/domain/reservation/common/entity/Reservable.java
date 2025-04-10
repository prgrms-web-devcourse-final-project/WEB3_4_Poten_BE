package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.common.entity;

import java.time.LocalDateTime;

public interface Reservable {
    LocalDateTime getStartTime();
    LocalDateTime getEndTime();
    int getPartySize();
}
