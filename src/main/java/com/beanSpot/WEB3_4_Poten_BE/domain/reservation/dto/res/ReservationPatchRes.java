package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
public class ReservationPatchRes {
	// 수정된 예약 정보를 클라이언트에게 반환할때의 데이터
	private Long reservationId;
	private ReservationStatus status;
	private LocalDate reservationDate;
	private LocalTime startTime;
	private LocalTime endTime;
}
