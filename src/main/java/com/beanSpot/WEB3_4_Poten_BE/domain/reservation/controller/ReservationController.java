package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    // ✅ 1. 예약 생성 API
    @PostMapping
    public ResponseEntity<ReservationPostRes> createReservation(@RequestBody ReservationPostReq dto) {
            ReservationPostRes response = reservationService.createReservation(dto);
            return ResponseEntity.ok(response); 
    }
    // ✅ 5. 특정 사용자의 예약 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReservationPostRes>> getUserReservations(@PathVariable Long userId) {
        List<ReservationPostRes> reservations = reservationService.getUserReservations(userId);
        return ResponseEntity.ok(reservations);
    }

    // ✅ 6. 특정 카페의 예약 조회 (날짜 기준 필터링)
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<ReservationPostRes>> getCafeReservations(
        @PathVariable Long cafeId,
        @RequestParam LocalDate date) {
        List<ReservationPostRes> reservations = reservationService.getCafeReservations(cafeId, date);
        return ResponseEntity.ok(reservations);
    }
}
