package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

public enum ReservationStatus {
	PENDING,    // 예약 대기
	CONFIRMED,  // 예약 확정 (결제 완료)
	CHECKED_IN, // 체크인 완료
	COMPLETED,  // 정상적으로 이용 완료
	CANCELLED,  // 예약 취소
	NO_SHOW     // 노쇼 (예약했지만 안 옴)
}
