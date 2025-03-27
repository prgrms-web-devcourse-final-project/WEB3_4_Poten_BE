package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

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

    // ✅ 예약 생성 API
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
}
