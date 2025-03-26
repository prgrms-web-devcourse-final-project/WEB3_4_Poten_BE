package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class ReservationPostReq {
	// 클라이언트가 예약 요청을 보낼 때 필요한 데이터
	private Long userId;
	private Long cafeId;
	private Long seatId;
	private LocalDate reservationDate;
	private LocalTime startTime;
	private LocalTime endTime;
	private Long paymentId;
}
