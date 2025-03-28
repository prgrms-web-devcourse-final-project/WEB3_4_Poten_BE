package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //특정 사용자의 예약 목록 조회
    List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    // 해당 사용시간에 몇명이 동시에 사용하는지 확인하는 쿼리
    @Query("""
        SELECT COUNT(r)
        FROM Reservation r
        WHERE r.cafe.id = :cafeId
        AND r.valid = true
        AND (:startDateTime < r.endTime AND :endDateTime > r.startTime)
    """)
    int countOverlappingReservations(
            @Param("cafeId") long cafeId,
            @Param("startDateTime") LocalDateTime startTime,
            @Param("endDateTime") LocalDateTime endTime
    );

    //특정카페 날짜별로 조회
    @Query("""
    SELECT r FROM Reservation r
    WHERE r.cafe.id = :cafeId
    AND r.startTime BETWEEN :startDateTime AND :endDateTime
    ORDER BY r.startTime DESC
""")
    List<Reservation> findByCafeIdAndDate(
            @Param("cafeId") Long cafeId,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime
    );
//이거 없어도 될듯?..
//    @Query("""
//        SELECT r FROM Reservation r
//        WHERE r.cafe.id = :cafeId
//        AND DATE(r.startTime) = :date
//        AND r.valid = true
//        ORDER BY r.startTime DESC
//    """)
//    List<Reservation> findValidReservationsByCafeIdAndDate(Long cafeId, LocalDate date);
}
