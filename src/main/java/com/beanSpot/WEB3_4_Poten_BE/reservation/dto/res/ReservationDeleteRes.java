package com.beanSpot.WEB3_4_Poten_BE.reservation.dto.res;

import com.beanSpot.WEB3_4_Poten_BE.reservation.entity.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationDeleteRes {
	// 예약이 성공적으로 취소되었는지 클라이언트에게 반환할때의 데이터
	private Long reservationId;
	private ReservationStatus status;
	private String message;
}
