package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;

import lombok.Builder;

@Builder
public record CafeReservationRes(
	Long id,
	//String username, 유저 관련정보 추후 추가하기
	LocalDateTime startTime,
	LocalDateTime endTime,
	ReservationStatus status,
	LocalDateTime createdAt
	//Long paymentId
) {
	public static CafeReservationRes from(Reservation reservation) {
		return CafeReservationRes.builder()
			.id(reservation.getId())
			.startTime(reservation.getStartTime())
			.endTime(reservation.getEndTime())
			.status(reservation.getStatus())
			.createdAt(reservation.getCreatedAt())
			.build();
	}
}
