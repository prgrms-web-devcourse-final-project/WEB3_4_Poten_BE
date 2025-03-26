package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

	@Column(nullable = false)
	private LocalDate reservationDate; // 예약 날짜

	@Column(nullable = false)
	private LocalTime startTime; // 예약 시작 시간

	@Column(nullable = false)
	private LocalTime endTime; // 예약 종료 시간

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ReservationStatus status; // 예약 상태 (Enum)

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
	public void updateReservationTime(LocalTime newStartTime, LocalTime newEndTime) {
		if (this.status != ReservationStatus.PENDING && this.status != ReservationStatus.CONFIRMED) {
			throw new IllegalStateException("진행 중이거나 종료된 예약은 변경할 수 없습니다.");
		}
		this.startTime = newStartTime;
		this.endTime = newEndTime;
	}

	// 좌석 변경 메서드
	public void updateSeat(Long newSeatId) {
		if (this.status != ReservationStatus.PENDING && this.status != ReservationStatus.CONFIRMED) {
			throw new IllegalStateException("진행 중이거나 종료된 예약은 좌석을 변경할 수 없습니다.");
		}
		this.seatId = newSeatId;
	}

	// 예약 상태 변경 메서드
	public void updateStatus(ReservationStatus newStatus) {
		this.status = newStatus;
	}
}
