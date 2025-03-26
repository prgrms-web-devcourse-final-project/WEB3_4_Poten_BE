package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
		indexes = {
				@Index(name = "idx_column1_column2", columnList = "column1, column2")
		}
)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 예약 ID

	@Column(nullable = false)
	private Long paymentId; // 결제 ID (결제 시스템 연동)

	@Column(nullable = false)
	private Long userId; // 예약한 사용자 ID

	@Column(nullable = false)
	private Long cafeId; // 카페 ID

	@Column(nullable = false)
	private Long seatId; // 예약한 좌석 ID

//	@Column(nullable = false)
//	private LocalDate reservationDate; // 예약 날짜

	//TODO: 날짜시간 으로 하면좋을지 시간으로 하면 좋을지
	@Column(nullable = false)
	private LocalDateTime startTime; // 예약 시작 시간

	@Column(nullable = false)
	private LocalDateTime endTime; // 예약 종료 시간

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status; // 예약 상태 (Enum)

	// valid = true 일때 유효한 예약
	@Column(nullable = false)
	private Boolean valid;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt; // 생성된 시간

	@Column(nullable = false)
	private LocalDateTime updatedAt; // 마지막 수정된 시간

	// 예약 생성 시 자동으로 createdAt, updatedAt 설정
	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// 예약 수정 시 updatedAt 자동 갱신
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	// 예약 취소 메서드 (CHECKED_IN 상태일 때 취소 불가)
	public void cancelReservation() {
		if (this.status == ReservationStatus.CHECKED_IN) {
			throw new IllegalStateException("체크인된 예약은 취소할 수 없습니다.");
		}
		this.status = ReservationStatus.CANCELLED;
	}

	// 예약 시간 변경 메서드 (시작/종료 시간 변경 가능)
	public void updateReservationTime(LocalDateTime newStartTime, LocalDateTime newEndTime) {
		if (this.status != ReservationStatus.CONFIRMED) {
			throw new IllegalStateException("진행 중이거나 종료된 예약은 변경할 수 없습니다.");
		}
		this.startTime = newStartTime;
		this.endTime = newEndTime;
	}

	// 좌석 변경 메서드
	public void updateSeat(Long newSeatId) {
		if (this.status != ReservationStatus.CONFIRMED) {
			throw new IllegalStateException("진행 중이거나 종료된 예약은 좌석을 변경할 수 없습니다.");
		}
		this.seatId = newSeatId;
	}

	// 예약 상태 변경 메서드
	public void updateStatus(ReservationStatus newStatus) {
		this.status = newStatus;
		this.valid = newStatus.isValid();
	}
	@Builder
	public Reservation(Long paymentId, Long userId, Long cafeId, Long seatId,
					   LocalDateTime startTime, LocalDateTime endTime,
					   ReservationStatus status) {
		this.paymentId = paymentId;
		this.userId = userId;
		this.cafeId = cafeId;
		this.seatId = seatId;

		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.valid = status.isValid(); // ✅ 예약 생성 시 상태에 따라 valid 자동 설정
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
}
