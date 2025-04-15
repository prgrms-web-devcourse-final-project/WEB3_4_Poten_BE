package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    //이거 커서 기반 페이징 적용
    @Query("""
        SELECT r FROM Reservation r
        WHERE r.member.id = :memberId
        AND (:cursorId IS NULL OR r.id < :cursorId)
        ORDER BY r.id DESC
        LIMIT :limit
    """)
    List<Reservation> findReservationsByMemberId(
            @Param("memberId") Long memberId,
            @Param("cursorId") Long cursorId,
            @Param("limit") int limit
    );

    String SELECT_OVERLAPPING_RESERVATIONS = """
        SELECT r
        FROM Reservation r
        WHERE r.cafe.id = :cafeId
        AND (r.startTime < :endTime)
        AND (r.endTime > :startTime)
        AND (r.valid = true)
    """;

    String SELECT_OVERLAPPING_RESERVATIONS_UPDATE =
            SELECT_OVERLAPPING_RESERVATIONS +
            """
            AND (:reservationId IS NULL OR :reservationId != r.id)
            """;

    // 락이 없는 버전
    @Query(SELECT_OVERLAPPING_RESERVATIONS)
    List<Reservation> getOverlappingReservations(
            @Param("cafeId") long cafeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );


    // 락이 있는 버전
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(SELECT_OVERLAPPING_RESERVATIONS_UPDATE)
    List<Reservation> getOverlappingReservationsWithLock(
            @Param("cafeId") long cafeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("reservationId") Long reservationIdForUpdate
    );

    default List<Reservation> getOverlappingReservationsWithLock(
            Long cafeId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return getOverlappingReservationsWithLock(cafeId, startTime, endTime, null);
    }

    //특정카페 날짜별로 조회
    @Query("""
    SELECT r FROM Reservation r
    WHERE r.cafe.id = :cafeId
    AND (r.startTime < :endTime AND :startTime <= r.startTime)
    ORDER BY r.startTime
    """)
    List<Reservation> findByCafeIdAndDate(
            @Param("cafeId") Long cafeId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

//    @Query("""
//    SELECT r FROM Reservation r
//    WHERE r.cafe.id = :cafeId AND DATE(r.startTime) = :date
//    ORDER BY r.startTime
//    """)
//    List<Reservation> findByCafeAndDate(@Param("cafeId") Long cafeId, @Param("date") LocalDate date);

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
