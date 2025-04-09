package com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 환불 요청 및 처리 정보를 저장하는 엔티티
 *
 * @author -- 김남우 --
 * @since -- 4월 8일 --
 */
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false)
    private Long refundAmount;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime refundedAt;

    @Column(columnDefinition = "TEXT")
    private String reason;
}
