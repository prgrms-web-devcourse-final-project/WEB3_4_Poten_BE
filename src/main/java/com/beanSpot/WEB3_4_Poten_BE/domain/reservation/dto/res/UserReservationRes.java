package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserReservationRes {
	private Long reservationId;
	//private String userName;
	private ReservationStatus status;
	private LocalDateTime startTime;
	private LocalDateTime endTime;
	private String cafeName;
	private String seatNumber;


	public static UserReservationRes from(Reservation reservation) {
		return UserReservationRes.builder()
			.reservationId(reservation.getId())
			.status(reservation.getStatus())
			.startTime(reservation.getStartTime())
			.endTime(reservation.getEndTime())
			.cafeName(reservation.getCafe().getName())
			.seatNumber(reservation.getSeat().getSeatNumber())
			//.userName(userName)
			.build();
	}
}
