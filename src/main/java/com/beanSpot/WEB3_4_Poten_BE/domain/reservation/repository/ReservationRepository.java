package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Seat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //특정 사용자의 예약 목록 조회
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    // 예약할 좌석, 시작시간, 끝나는시간을 받고 겹치는 시간이 있는지 확인하는 쿼리
    //TODO: 서비스에서 처리하는게 좋을자 아니면 repository 에서 처리하면 좋을지 물어보기
    @Query("""
        SELECT COUNT(r) > 0
        FROM Reservation r
        WHERE r.seatId = :seatId
        AND r.valid = true
        AND (:startDateTime < r.endDateTime AND :endDateTime > r.startDateTime)
    """)
    boolean existsOverlappingReservation(
        @Param("seatId") long seatId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    //특정카페 날짜별로 조회
    @Query("""
    SELECT r FROM Reservation r
    WHERE r.cafeId = :cafeId
    AND r.startTime BETWEEN :startDateTime AND :endDateTime
    ORDER BY r.startTime DESC
""")
    List<Reservation> findByCafeIdAndDate(
        @Param("cafeId") Long cafeId,
        @Param("startDateTime") LocalDateTime startDateTime,
        @Param("endDateTime") LocalDateTime endDateTime
    );

    @Query("""
        SELECT r FROM Reservation r
        WHERE r.seatId = :seatId
        AND DATE(r.startTime) = :date
        AND r.valid = true
        ORDER BY r.startTime DESC
    """)
    List<Reservation> findValidReservationsBySeatAndDate(Long seatId, LocalDate date);
}