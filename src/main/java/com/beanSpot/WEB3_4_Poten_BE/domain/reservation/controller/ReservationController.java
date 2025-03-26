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
            ReservationPostRes response = reservationService.createReservation(dto);
            return ResponseEntity.ok(response); 
    }
}
