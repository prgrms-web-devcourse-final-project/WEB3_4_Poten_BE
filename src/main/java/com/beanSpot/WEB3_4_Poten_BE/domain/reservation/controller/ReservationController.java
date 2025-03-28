package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.CafeReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationDetailRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.UserReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    // ✅ 1. 예약 생성 API
    @PostMapping
    public ResponseEntity<ReservationPostRes> createReservation(@RequestBody ReservationPostReq dto) {
        //TODO: 추후 리팩토링 하기
        if (dto.isValidTimeRange()) throw new RuntimeException("끝시간이 시작시간보다 앞에있을수 없습니다");

        ReservationPostRes response = reservationService.createReservation(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reservationId}")
    public ResponseEntity<ReservationPostRes> updateReservation(
            @RequestBody ReservationPostReq dto,
            @PathVariable Long reservationId
    ) {
        //TODO: 추후 리팩토링 하기
        if (dto.isValidTimeRange()) throw new RuntimeException("끝시간이 시작시간보다 앞에있을수 없습니다");

        ReservationPostRes response = reservationService.updateReservation(reservationId, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @RequestBody ReservationPostReq dto,
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(reservationId);
        return ResponseEntity.ok().build();
    }

    //예약 디테일 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailRes> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        ReservationDetailRes res = reservationService.getReservationDetail(reservationId);
        return ResponseEntity.ok(res);
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
