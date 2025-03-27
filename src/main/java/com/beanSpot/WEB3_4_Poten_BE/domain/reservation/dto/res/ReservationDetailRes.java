package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

public record ReservationDetailRes(
        Long id,
        //String username,
        Long cafeId,
        String cafeName,
        int seatNumber,
        Long paymentId
) {

}