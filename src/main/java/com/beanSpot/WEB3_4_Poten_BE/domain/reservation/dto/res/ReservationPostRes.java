package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationPostRes {
	// 예약이 성공적으로 생성되었을 때 클라이언트에게 반환되는 데이터
	private Long reservationId;
	private ReservationStatus status;
	private LocalDate reservationDate;
	private LocalDateTime startTime;
	private LocalDateTime endTime;

	public static ReservationPostRes from(Reservation reservation) {
		return ReservationPostRes.builder()
				.reservationId(reservation.getId())
				.status(reservation.getStatus())
				.reservationDate(reservation.getStartTime().toLocalDate())  // 날짜 부분만 추출
				.startTime(reservation.getStartTime())  // 시간 부분만 추출
				.endTime(reservation.getEndTime())  // 시간 부분만 추출
				.build();
	}
}
