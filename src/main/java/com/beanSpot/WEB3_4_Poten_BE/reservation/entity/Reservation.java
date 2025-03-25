package com.beanSpot.WEB3_4_Poten_BE.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
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
}
