package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserId(Long userId); // 특정 사용자의 예약 목록 조회

    //예약할 좌석, 시작시간, 끝나는시간을 받고 겹치는 시간이 있는지 확인하는 쿼리
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
}
