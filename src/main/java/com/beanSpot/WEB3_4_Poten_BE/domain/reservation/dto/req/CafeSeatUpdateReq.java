package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.NotNull;

//카페 업데이트 요청 dto
public class CafeSeatUpdateReq {
    @NotNull
    long id;
    @NotNull
    int seatNumber;
    @NotNull
    int capacity;
}
