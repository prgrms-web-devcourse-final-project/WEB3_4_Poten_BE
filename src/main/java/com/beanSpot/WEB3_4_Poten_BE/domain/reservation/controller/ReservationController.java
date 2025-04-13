package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.SeatCountReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.TimeSlotsReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.AvailableSeatsCount;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.CafeReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationDetailRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.TimeSlot;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.UserReservationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi {

    private final ReservationService reservationService;

    // 예약 생성 API
    @Override
    @PostMapping("/{cafeId}")
    public ResponseEntity<ReservationPostRes> createReservation(
        @PathVariable Long cafeId,
        @Valid @RequestBody ReservationPostReq dto,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReservationPostRes response = reservationService.createReservation(cafeId, dto, securityUser.getMember());
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationPostRes> updateReservation(
        @Valid @RequestBody ReservationPatchReq dto,
        @PathVariable Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReservationPostRes response = reservationService.updateReservation(reservationId, dto, LocalDateTime.now(), securityUser.getMember());
        return ResponseEntity.ok(response);
    }

    @Override
    @PatchMapping("/checkout/{reservationId}")
    public ResponseEntity<Void> checkout(
        @PathVariable Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        reservationService.checkout(reservationId, now, securityUser.getMember());
        return ResponseEntity.ok().build();
    }

    @Override
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
        @PathVariable Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        reservationService.cancelReservation(reservationId, LocalDateTime.now(), securityUser.getMember());
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

    @Override
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailRes> getReservationDetail(
        @PathVariable Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ReservationDetailRes res = reservationService.getReservationDetail(reservationId, securityUser.getMember());
        return ResponseEntity.ok(res);
    }

    @Override
    @GetMapping("/user")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(
        @AuthenticationPrincipal SecurityUser securityUser,
        @RequestParam(required = false) Long cursorId
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<UserReservationRes> reservations = reservationService.getUserReservations(securityUser.getMember().getId(), cursorId);
        return ResponseEntity.ok(reservations);
    }

    @Override
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
        @PathVariable Long cafeId,
        @RequestParam LocalDate date
    ) {
        List<CafeReservationRes> res = reservationService.getCafeReservations(cafeId, date);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/myreservations")
    public ResponseEntity<List<UserReservationRes>> getMyReservations(
        @AuthenticationPrincipal SecurityUser securityUser,
        @RequestParam(required = false) Long cursorId
    ) {
        if (securityUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member authenticatedMember = securityUser.getMember();
        List<UserReservationRes> reservations = reservationService.getUserReservations(
            authenticatedMember.getId(), cursorId);
        return ResponseEntity.ok(reservations);
    }
}