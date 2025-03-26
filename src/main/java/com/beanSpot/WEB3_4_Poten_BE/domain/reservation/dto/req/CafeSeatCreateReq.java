package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.NotNull;

//카페 생성 요청 dto
public class CafeSeatCreateReq {
    @NotNull
    int seatNumber;
    int capacity = 1;
}
