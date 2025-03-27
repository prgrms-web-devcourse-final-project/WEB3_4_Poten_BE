package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPatchRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    // ✅ 1. 예약 생성
    //TODO: 동시성 문제 해결하기
    @Transactional
    public ReservationPostRes createReservation(ReservationPostReq dto) {
        // 예약 가능 여부 확인
        boolean isAvailable = !reservationRepository.existsOverlappingReservation(
                dto.getSeatId(), dto.getStartTime(), dto.getEndTime());

        if (!isAvailable) {
            throw new IllegalStateException("해당 시간에 이미 예약된 좌석입니다.");
        }

        Reservation reservation = Reservation.builder()
                .paymentId(dto.getPaymentId())
                .userId(dto.getUserId())
                .cafeId(dto.getCafeId())
                .seatId(dto.getSeatId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();
        reservationRepository.save(reservation);

        return ReservationPostRes.from(reservation);
    }

    // ✅ 2. 예약 수정
    @Transactional
    public ReservationPostRes updateReservation(Long reservationId, ReservationPostReq dto) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));

        // 예약 가능 여부 확인 (변경된 시간 기준)
        boolean isAvailable = !reservationRepository.existsOverlappingReservation(
                dto.getSeatId(), dto.getStartTime(), dto.getEndTime());

        if (!isAvailable) {
            throw new RuntimeException("해당 시간에 이미 예약된 좌석입니다.");
        }

        // 예약 정보 업데이트
//        reservation.update(dto.getPaymentId(), dto.getUserId(), dto.getCafeId(),
//                dto.getSeatId(), dto.getStartTime(), dto.getEndTime());

        return ReservationPostRes.from(reservation);
    }
    // ✅ 3. 예약 취소
    // ✅ 4. 예약 상세 조회
    // ✅ 5. 특정 사용자의 예약 목록 조회
    // ✅ 6. 특정 카페의 예약 조회
}
