package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class ReservationPatchReq {
	// 예약 시간 변경을 원할 경우 필요한 데이터
	private Long cafeId;
	private LocalTime startTime; // 변경할 시작 시간 (선택 사항)
	private LocalTime endTime;   // 변경할 종료 시간 (선택 사항)
}
