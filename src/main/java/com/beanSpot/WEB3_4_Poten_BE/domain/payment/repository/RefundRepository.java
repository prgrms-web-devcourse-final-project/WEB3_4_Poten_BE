package com.beanSpot.WEB3_4_Poten_BE.domain.payment.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.payment.entity.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefundRepository  extends JpaRepository<Refund, Long> {

}
