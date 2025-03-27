package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    public Optional<Seat> findById(long id);
}
