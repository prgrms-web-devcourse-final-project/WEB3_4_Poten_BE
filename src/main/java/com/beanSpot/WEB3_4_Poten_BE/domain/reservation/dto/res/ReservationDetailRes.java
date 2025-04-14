package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReservationDetailRes(
        Long id,
        Long cafeId,
        String cafeName,
        String cafeAddress,
        String cafePhoneNumber,
        LocalDateTime startTime,
        LocalDateTime endTime,
        ReservationStatus status,
        Integer partySize,
        LocalDateTime createdAt
        //Long paymentId
) {
    public static ReservationDetailRes from(Reservation reservation) {
        return ReservationDetailRes.builder()
                .id(reservation.getId())
                .cafeId(reservation.getCafe().getCafeId())
                .cafeName(reservation.getCafe().getName())
                .cafeAddress(reservation.getCafe().getAddress())
                .cafePhoneNumber(reservation.getCafe().getPhone())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .partySize(reservation.getPartySize())
                .createdAt(reservation.getCreatedAt())
                .build();
    }
}