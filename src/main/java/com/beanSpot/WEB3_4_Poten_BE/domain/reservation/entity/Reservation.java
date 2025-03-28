package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
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
				@Index(columnList = "seat_id, start_time, end_time")
		}
)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 예약 ID

	//Payment 생기면 이걸로 적용
	//	@JoinColumn(nullable = false)
	//	private Payment payment;

	@Column(nullable = false)
	private Long paymentId; // 결제 ID (결제 시스템 연동)

	//유저 생기면 이걸로 사용
//	@JoinColumn(nullable = false)
//	private User user;

	@Column(nullable = false)
	private Long userId; // 예약한 사용자 ID

	@JoinColumn(nullable = false)
	private Cafe cafe;

	@JoinColumn(nullable = false)
	private Seat seat;

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
		if (this.valid == null) { // valid 값이 설정되지 않았다면 기본값 설정
			this.valid = this.status.isValid();
		}
	}

	// 예약 수정 시 updatedAt 자동 갱신
	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	//예약 업데이트 메소드
	public void update(Seat seat, LocalDateTime startTime, LocalDateTime endTime) {
		this.seat = seat;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	// 예약 취소 메서드 (CHECKED_IN 상태일 때 취소 불가)
	public void cancelReservation() {
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

	// 예약 상태 변경 메서드
	public void updateStatus(ReservationStatus newStatus) {
		this.status = newStatus;
		this.valid = newStatus.isValid();
	}

	// 시작하기 beforeStartMinutes 전인지 판별, 변경이나 삭제 가능한지 확인하는 메소드
	// 예약 변경 불가능 여부 확인 메소드
	public boolean cannotModify(int beforeStartMinutes) {
		if (this.startTime == null) {
			throw new IllegalStateException("예약 시작 시간이 설정되지 않았습니다.");
		}

		LocalDateTime now = LocalDateTime.now();

		// 예약 변경 불가능: 예약 시간이 beforeStartMinutes 이내로 남았으면 true
		return Duration.between(now, this.startTime).toMinutes() < beforeStartMinutes;
	}


	@Builder
	public Reservation(Long paymentId, Long userId, Cafe cafe, Seat seat,
					   LocalDateTime startTime, LocalDateTime endTime,
					   ReservationStatus status) {
		this.paymentId = paymentId;
		this.userId = userId;
		this.cafe = cafe;
		this.seat = seat;

		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.valid = status.isValid(); // ✅ 예약 생성 시 상태에 따라 valid 자동 설정
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
}


