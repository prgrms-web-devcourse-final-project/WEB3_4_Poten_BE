package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import java.time.LocalDateTime;

public interface TimePeriod {
    LocalDateTime startTime();
    LocalDateTime endTime();
}
