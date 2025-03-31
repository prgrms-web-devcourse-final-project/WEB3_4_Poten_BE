package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
public class ReservationPatchReq {
	@NotNull(message = "시작 시간은 필수입니다.")
	@Future(message = "시작 시간은 현재 시간 이후여야 합니다.")
	private LocalDateTime startTime;

	@NotNull(message = "종료 시간은 필수입니다.")
	@Future(message = "종료 시간은 현재 시간 이후여야 합니다.")
	private LocalDateTime endTime;

	// 추가 커스텀 검증을 위한 메서드
	public boolean isValidTimeRange() {
		return startTime != null && endTime != null &&
				!startTime.isAfter(endTime);
	}
}
