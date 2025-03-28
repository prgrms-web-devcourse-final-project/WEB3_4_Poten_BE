package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.CafeReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPatchRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.UserReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Seat;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.SeatRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;
    private final MemberRepository memberRepository;
    private final CafeRepository cafeRepository;

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
    // ✅ 3. 예약 취소
    // ✅ 4. 예약 상세 조회
    // ✅ 5. 특정 사용자의 예약 목록 조회
    @Transactional(readOnly = true)
    public List<UserReservationRes> getUserReservations(Long userId) {
        List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        // ✅ userId로 사용자 이름 조회 (예제: "홍길동" 반환)
        String userName = memberRepository.findById(userId)
            .map(member -> member.getName())
            .orElse("알 수 없음");

        return reservations.stream()
            .map(reservation -> UserReservationRes.from(reservation, userName))
            .collect(Collectors.toList());
    }

    // ✅ 6. 특정 카페의 예약 조회 (날짜 기준 필터링)
    @Transactional(readOnly = true)
    public List<CafeReservationRes> getCafeReservations(Long cafeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        // TODO: cafeId가 존재하는지 검증하는 로직
        boolean cafeExists = cafeRepository.existsById(cafeId);
        if (!cafeExists) {
            throw new RuntimeException("존재하지 않는 카페Id 입니다");
        }

        List<Reservation> reservations = reservationRepository.findByCafeIdAndDate(cafeId, startOfDay, endOfDay);
        return reservations.stream()
            .map(CafeReservationRes::from)
            .toList();
    }
}
