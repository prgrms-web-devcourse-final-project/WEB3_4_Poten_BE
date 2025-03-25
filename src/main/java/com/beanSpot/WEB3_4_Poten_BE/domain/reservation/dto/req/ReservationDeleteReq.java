package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationDeleteReq {
	// 예약 취소 시 필요한 데이터
	private Long reservationId;
	private Long userId;
}
