package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

//TODO: 상태추가시 isValid 함수도 변경되야 합니다!!
public enum ReservationStatus {
	CONFIRMED,  // 예약 완료 (결제 완료)
	CANCELLED,  // 예약 취소
	NO_SHOW;     // 노쇼 (예약했지만 안 옴)

	//TODO: 이거 추후수정
	// ✅ 현재 상태에서 다른 사람이 예약 가능한지 여부 반환
	public boolean isValid() {
		return this == CONFIRMED;
	}
}

//2~3시 사이에 예약해놓고 이용중인데 그러면 체크인상태일텐데, 상태만 가지고 하면 예약 불가능 상태가 되니까
// 다른사람이 3시 이후에 미리 예약할 수 있어야 하는데 못하게 된다.

// 2~3 사이에 예약잡혀있다.
//(4~5 사이에 예약있는데) -> 취소, 노쇼 -> isValid = False
// 정상적 예약 true
// 3시1분에서 3시 58분 까지 예약잡고싶다.
// 3시30분 에서 4시30분 사이에 예약하면 예약불가
//