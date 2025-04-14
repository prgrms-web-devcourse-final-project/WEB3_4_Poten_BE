package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi{
    private final ReservationService reservationService;

    // 예약 생성 API
    @Override
    @PostMapping("/{cafeId}")
    public ResponseEntity<ReservationPostRes> createReservation(
            @PathVariable Long cafeId,
            @Valid @RequestBody ReservationPostReq dto,
            @AuthenticationPrincipal SecurityUser user
    ) {
        //TODO: 추후 리팩토링 하기
        ReservationPostRes response = reservationService.createReservation(cafeId, dto, user.getMember());
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationPostRes> updateReservation(
            @Valid @RequestBody ReservationPatchReq dto,
            @PathVariable Long reservationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        ReservationPostRes response = reservationService.updateReservation(reservationId, dto, user.getMember());
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/checkout/{reservationId}")
    public ResponseEntity<Void> checkout(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        reservationService.checkout(reservationId, user.getMember());
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        reservationService.cancelReservation(reservationId, user.getMember());
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/availableCounts/{cafeId}")
    public ResponseEntity<AvailableSeatsCount> getAvailableSeatsCount(
            @PathVariable Long cafeId,
            @Valid @ModelAttribute SeatCountReq req
    ) {
        AvailableSeatsCount res = reservationService.getAvailableSeatsCount(cafeId, req.startTime(), req.endTime());
        return ResponseEntity.ok(res);
    }

    @Override
    @GetMapping("/availableTimeSlots/{cafeId}")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(
            @PathVariable Long cafeId,
            @Valid @ModelAttribute TimeSlotsReq req
    ) {
        List<TimeSlot> res = reservationService.getAvailableTimeSlots(cafeId, req);
        return ResponseEntity.ok(res);
    }

    //예약 디테일 조회
    @Override
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailRes> getReservationDetail(
            @PathVariable Long reservationId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        ReservationDetailRes res = reservationService.getReservationDetail(reservationId, user.getMember());
        return ResponseEntity.ok(res);
    }

    //특정 사용자의 예약 목록 조회
    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(
            @RequestParam(required = false) Long cursorId,
            @AuthenticationPrincipal SecurityUser user
    ) {
        List<UserReservationRes> reservations = reservationService.getUserReservations(user.getMember().getId(), cursorId);
        return ResponseEntity.ok(reservations);
    }


    //특정 카페의 예약 조회 (날짜 기준 필터링)
    @Override
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
            @PathVariable Long cafeId,
            @RequestParam LocalDate date,
            @AuthenticationPrincipal SecurityUser user)  {

        List<CafeReservationRes> res = reservationService.getCafeReservations(cafeId, date, user.getId());
        return ResponseEntity.ok(res);
    }
}
