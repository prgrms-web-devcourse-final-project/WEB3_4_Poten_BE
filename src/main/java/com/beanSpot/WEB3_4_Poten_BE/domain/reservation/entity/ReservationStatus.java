package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

//TODO: 상태추가시 isValid 함수도 변경되야 합니다!!
public enum ReservationStatus {
	CONFIRMED,  // 예약 확정 (결제 완료)
	CANCELLED,  // 예약 취소
	FINISHED; // 사용 완료


	//TODO: 이거 추후수정
	// ✅ 현재 상태에서 다른 사람이 예약 가능한지 여부 반환
	public boolean isValid() {
		return this == CONFIRMED;
	}
}