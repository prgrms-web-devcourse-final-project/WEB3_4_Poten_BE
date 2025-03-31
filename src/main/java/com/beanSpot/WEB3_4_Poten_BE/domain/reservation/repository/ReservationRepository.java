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

    String SELECT_OVERLAPPING_RESERVATIONS = """
        SELECT r
        FROM Reservation r
        WHERE r.cafe.id = :cafeId
        AND (r.startTime < :endTime)
        AND (r.endTime > :startTime)
        AND (r.valid = true)
        AND (:reservationId IS NULL OR :reservationId != r.id)
    """;

    // 락이 없는 버전
    @Query(SELECT_OVERLAPPING_RESERVATIONS)
    List<Reservation> getOverlappingReservations(
            @Param("cafeId") long cafeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("reservationId") Long reservationIdForUpdate
    );

    // 락이 있는 버전
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(SELECT_OVERLAPPING_RESERVATIONS)
    List<Reservation> getOverlappingReservationsWithLock(
            @Param("cafeId") long cafeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("reservationId") Long reservationIdForUpdate
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
