package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.CafeReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.UserReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    private final CafeRepository cafeRepository;

    // ✅ 1. 예약 생성 API
    @PostMapping
    public ResponseEntity<ReservationPostRes> createReservation(@RequestBody ReservationPostReq dto) {
            ReservationPostRes response = reservationService.createReservation(dto);
            return ResponseEntity.ok(response); 
    }
    // ✅ 5. 특정 사용자의 예약 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(@PathVariable Long userId) {
        List<UserReservationRes> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }


    // ✅ 6. 특정 카페의 예약 조회 (날짜 기준 필터링)
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
        @PathVariable Long cafeId,
        @RequestParam LocalDate date)  {
        List<CafeReservationRes> res = reservationService.getCafeReservations(cafeId, date);
        return ResponseEntity.ok(res);
    }
}
