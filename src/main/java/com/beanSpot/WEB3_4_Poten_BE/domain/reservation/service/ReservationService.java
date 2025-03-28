package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
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
    private final CafeRepository cafeRepository;

    // ✅ 1. 예약 생성
    //TODO: 동시성 문제 해결하기
    @Transactional
    public ReservationPostRes createReservation(ReservationPostReq dto) {

        //좌석 조회
        Cafe cafe = cafeRepository.findById(dto.getCafeId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카페 Id 입니다."));

        // 예약 가능 여부 확인
        int overlapCount = reservationRepository.countOverlappingReservations(
                dto.getCafeId(), dto.getStartTime(), dto.getEndTime());

        if (overlapCount >= cafe.getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        Reservation reservation = Reservation.builder()
                .userId(dto.getUserId())
                .cafe(cafe)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();
        reservationRepository.save(reservation);

        return ReservationPostRes.from(reservation);
    }

    // ✅ 2. 예약 수정
    //TODO: 동시성 문제 해결하기
    //TODO: 검증하기
    //TODO: 중간에 체크아웃 하는로직??
    @Transactional
    public ReservationPostRes updateReservation(Long reservationId, ReservationPostReq dto) {

        //예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));

        //원래 예약시간 1시간 전에만 변경 가능하게 체크
        if (reservation.cannotModify(60)) {
            throw new RuntimeException("변경이 불가능합니다. 점주님께 문의하세요.");
        }

        // 예약 가능 여부 확인
        int overlapCount = reservationRepository.countOverlappingReservations(
                dto.getCafeId(), dto.getStartTime(), dto.getEndTime());

        //만약 변경예약이 원래예약과 겹치면 overlapCount 에서 1을 빼줌
        overlapCount -= reservation.isOverlapping(dto.getStartTime(), dto.getEndTime()) ? 1 : 0;

        if (overlapCount >= reservation.getCafe().getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        // 예약 정보 업데이트
        reservation.update(dto.getStartTime(), dto.getEndTime());

        return ReservationPostRes.from(reservation);
    }

    // ✅ 3. 예약 취소
    @Transactional
    public void cancelReservation(Long reservationId) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        //시작시간 60분전 취소 불가능
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
    @Transactional(readOnly = true)
    public List<UserReservationRes> getUserReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);

        return reservations.stream()
            .map(UserReservationRes::from)
            .collect(Collectors.toList());
    }

    // ✅ 6. 특정 카페의 예약 조회 (날짜 기준 필터링)
    @Transactional(readOnly = true)
    public List<CafeReservationRes> getCafeReservations(Long cafeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Reservation> reservations = reservationRepository.findByCafeIdAndDate(cafeId, startOfDay, endOfDay);
        return reservations.stream()
            .map(CafeReservationRes::from)
            .toList();
    }
}
