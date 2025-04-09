package com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String orderId;	// 토스 주문ID (예: "ORDER_20250306_ABC123")

//	@Column(nullable = false)
//	private Long userId;

	@Column(nullable = false)
	private String paymentKey;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private LocalDateTime paySuccessDate;

	private LocalDateTime payCancelDate;

	@Column(nullable = false)
	private String method;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PaymentStatus paymentStatus; // SUCCESS, CANCEL

	@OneToMany(mappedBy = "payment", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Refund> refunds = new ArrayList<>();

	/**
	 *  Payment 객체의 상태를 변경하고 취소 시간을 기록하기 위한 메서드
	 */
	public void cancel(LocalDateTime canceledAt) {
		this.paymentStatus = PaymentStatus.CANCEL;
		this.payCancelDate = canceledAt;
	}
}