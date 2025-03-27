package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Seat;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    public Optional<Seat> findById(long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 비관적 쓰기 락
    @QueryHints({
            @QueryHint(name = "javax.persistence.lock.timeout", value = "5000") // 5초(5000ms) 대기
    })
    Optional<Seat> findWithLockById(Long id);
}
