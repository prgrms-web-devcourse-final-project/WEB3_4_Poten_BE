package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.vo;

import java.time.LocalDateTime;

public record TimeWithPartySize(
        LocalDateTime time,
        int partySize
) {
}
