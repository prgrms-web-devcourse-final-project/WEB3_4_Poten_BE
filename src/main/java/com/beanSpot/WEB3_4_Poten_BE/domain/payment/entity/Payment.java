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

	@Column(unique = true)
	private String tossOrderId;	// 토스 주문ID (예: "ORDER_20250306_ABC123")

	private String paymentKey;

	private Long paidAmount;

	private LocalDateTime paySuccessDate;

	private String method;
}