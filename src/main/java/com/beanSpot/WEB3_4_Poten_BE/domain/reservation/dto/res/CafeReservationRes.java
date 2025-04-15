package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;

import lombok.Builder;

@Builder
public record CafeReservationRes(
	Long id,
	String name,
	String email,
	LocalDateTime startTime,
	LocalDateTime endTime,
	ReservationStatus status,
	Integer partySize,
	LocalDateTime createdAt
	//Long paymentId
) {
	public static CafeReservationRes from(Reservation reservation) {
		return CafeReservationRes.builder()
			.id(reservation.getId())
				.name(reservation.getMember().getName())
				.email(reservation.getMember().getEmail())
			.startTime(reservation.getStartTime())
			.endTime(reservation.getEndTime())
			.status(reservation.getStatus())
				.partySize(reservation.getPartySize())
			.createdAt(reservation.getCreatedAt())
			.build();
	}
}
