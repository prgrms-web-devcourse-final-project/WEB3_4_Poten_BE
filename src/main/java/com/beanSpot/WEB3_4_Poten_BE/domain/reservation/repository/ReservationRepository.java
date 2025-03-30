package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //TODO: 멤버 추가시 수정 주석처리된거 되돌리기
    //List<Reservation> findByUserIdOrderByStartTimeDesc(Long userId);

    // 해당 사용시간에 몇명이 동시에 사용하는지 확인하는 쿼리
    String COUNT_OVERLAPPING_RESERVATIONS = """
        SELECT COUNT(r)
        FROM Reservation r
        WHERE r.cafe.id = :cafeId
        AND (r.startTime BETWEEN :openingTime AND :endDateTime)
        AND (r.endTime >= :startDateTime)
        AND r.valid = true
    """;

    // 락이 없는 버전
    @Query(COUNT_OVERLAPPING_RESERVATIONS)
    int countOverlappingReservations(
            @Param("cafeId") long cafeId,
            @Param("startDateTime") LocalDateTime startTime,
            @Param("endDateTime") LocalDateTime endTime,
            @Param("openingTime") LocalDateTime openingTime
    );

    // 락이 있는 버전
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(COUNT_OVERLAPPING_RESERVATIONS)
    int countOverlappingReservationsWithLock(
            @Param("cafeId") long cafeId,
            @Param("startDateTime") LocalDateTime startTime,
            @Param("endDateTime") LocalDateTime endTime,
            @Param("openingTime") LocalDateTime openingTime
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
