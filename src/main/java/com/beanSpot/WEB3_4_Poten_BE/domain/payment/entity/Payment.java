package com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

	@Column(nullable = false)
	private String paymentKey;

	@Column(nullable = false)
	private Long amount;

	@Column(nullable = false)
	private LocalDateTime paySuccessDate;

	@Column(nullable = false)
	private String method;

}