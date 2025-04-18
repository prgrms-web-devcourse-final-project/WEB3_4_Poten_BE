package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//TODO: 필요한 인덱스 추가하기
@Table(
		indexes = {
				@Index(columnList = "cafe_id, start_time, end_time"),
				@Index(columnList = "member_id, id")
		}
)
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 예약 ID

	// 추후 member 와 payment 추가하기

    @ManyToOne
	@JoinColumn(name = "cafe_id", nullable = false)
	private Cafe cafe;

	@ManyToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(nullable = false)
	private LocalDateTime startTime; // 예약 시작 시간

	@Column(nullable = false)
	private LocalDateTime endTime; // 예약 종료 시간

	@Column(nullable = false)
	private Integer partySize;

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

	public static Reservation of(ReservationPostReq dto, Cafe cafe, Member member) {
		return  Reservation.builder()
				.cafe(cafe)
				.member(member)
				.startTime(dto.getReservationTime().startTime())
				.endTime(dto.getReservationTime().endTime())
				.status(ReservationStatus.CONFIRMED)
				.partySize(dto.getPartySize())
				.build();
	}

	public ReservationStatus getStatus() {
		if (status.equals(ReservationStatus.CONFIRMED) && LocalDateTime.now().isAfter(endTime)) {
			return ReservationStatus.FINISHED;
		}
		return status;
	}

	public boolean isOwner(Long memberId) {
		return memberId != null && member.getId().equals(memberId);
	}

	//예약 업데이트 메소드
	public void update(LocalDateTime startTime, LocalDateTime endTime, int partySize) {
		updateReservationTime(startTime, endTime);
		this.partySize = partySize;
	}

	// 예약 시간 변경 메서드 (시작/종료 시간 변경 가능)
	public void updateReservationTime(LocalDateTime newStartTime, LocalDateTime newEndTime) {
		this.startTime = newStartTime;
		this.endTime = newEndTime;
	}

	// 예약 상태 변경 메서드
	public void updateStatus(ReservationStatus newStatus) {
		this.status = newStatus;
		updateValid(newStatus);
	}

	private void updateValid(ReservationStatus newStatus) {
		this.valid = newStatus.isValid();
	}

	// (예약 시작 시간) - (현재시간) >= beforeStartMinutes 이면 true -> 변경 가능
	public boolean isModifiable(LocalDateTime now, int minutesBeforeStart, Member member) {
		return this.valid &&
				isOwner(member) &&
				(Duration.between(now, this.startTime).toMinutes() >= minutesBeforeStart);
	}

	//체크아웃 시간 가능 유무
	public boolean isCheckoutTimeValid(LocalDateTime checkoutTime, Member member) {
		return this.valid &&
				isOwner(member) &&
				!checkoutTime.isBefore(this.startTime) && checkoutTime.isBefore(this.endTime);
	}

	public boolean isOwner(Member member) {
		return this.member.getId().equals(member.getId());
	}

	@Builder
	public Reservation(Cafe cafe,
					   LocalDateTime startTime, LocalDateTime endTime,
					   ReservationStatus status) {
		this.cafe = cafe;
		this.startTime = startTime;
		this.endTime = endTime;
		this.status = status;
		this.valid = status.isValid(); // ✅ 예약 생성 시 상태에 따라 valid 자동 설정
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}
}


