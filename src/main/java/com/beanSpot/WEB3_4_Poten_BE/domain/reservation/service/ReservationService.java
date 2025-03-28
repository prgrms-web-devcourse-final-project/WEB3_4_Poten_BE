package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationDetailRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPatchRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Seat;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.SeatRepository;
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
    private final SeatRepository seatRepository;

    // ✅ 1. 예약 생성
    //TODO: 동시성 문제 해결하기
    @Transactional
    public ReservationPostRes createReservation(ReservationPostReq dto) {

        //좌석 조회
        Seat seat = seatRepository.findWithLockById(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 좌석입니다."));

        // 예약 가능 여부 확인
        boolean isAvailable = !reservationRepository.existsOverlappingReservation(
                dto.getSeatId(), dto.getStartTime(), dto.getEndTime());

        if (!isAvailable) {
            throw new IllegalStateException("해당 시간에 이미 예약된 좌석입니다.");
        }

        Reservation reservation = Reservation.builder()
                .paymentId(dto.getPaymentId())
                .userId(dto.getUserId())
                .cafe(seat.getCafe())
                .seat(seat)
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

        //예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));

        //원래 예약시간 1시간 전에만 변경 가능하게 체크
        if (reservation.cannotModify(60)) {
            throw new RuntimeException("변경이 불가능합니다. 점주님께 문의하세요.");
        }

        //좌석 조회
        Seat seat = seatRepository.findWithLockById(dto.getSeatId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 좌석입니다."));

        // 예약 가능 여부 확인 (변경된 시간 기준)
        boolean isAvailable = !reservationRepository.existsOverlappingReservation(
                dto.getSeatId(), dto.getStartTime(), dto.getEndTime());

        if (!isAvailable) {
            throw new RuntimeException("해당 시간에 이미 예약된 좌석입니다.");
        }

        // 예약 정보 업데이트
        reservation.update(seat, dto.getStartTime(), dto.getEndTime());

        return ReservationPostRes.from(reservation);
    }

    // ✅ 3. 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        //시작시간 60분전 취소 불가능
        // 추후 수수료 내고 취소 하도록 변경
        if (!reservation.cannotModify(60)) {
            throw new RuntimeException("취소가 불가능합니다. 점주님께 문의하세요.");
        }

        reservation.cancelReservation();
    }

    // ✅ 4. 예약 상세 조회
    @Transactional(readOnly = true)
    public ReservationDetailRes getReservationDetail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        return ReservationDetailRes.from(reservation);
    }

    // ✅ 5. 특정 사용자의 예약 목록 조회
    // ✅ 6. 특정 카페의 예약 조회
}
