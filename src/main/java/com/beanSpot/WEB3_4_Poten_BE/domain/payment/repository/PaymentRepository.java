package com.beanSpot.WEB3_4_Poten_BE.domain.payment.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);
}