package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.NotNull;

public class CafeSeatCreateReq {
    @NotNull
    int seatNumber;
    int capacity = 1;
}
