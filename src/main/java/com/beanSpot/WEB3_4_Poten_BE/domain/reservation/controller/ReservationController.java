package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
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

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;
    //TODO: 추후삭제
    private final MemberRepository memberRepository;
    private Member member = Member.builder()
            .email("user0@google.com")
            .name("user0")
            .memberType(Member.MemberType.USER)
            .oAuthId("user0")
            .password("1234")
            .username("user0")
            .build();

    @PostConstruct
    public void initMember() {
        member = memberRepository.save(member);
    }
    // 끝


    // ✅ 1. 예약 생성 API
    @PostMapping("/{cafeId}")
    public ResponseEntity<ReservationPostRes> createReservation(
            @RequestParam Long cafeId,
            @RequestBody ReservationPostReq dto
    ) {
        //TODO: 추후 리팩토링 하기
        ReservationPostRes response = reservationService.createReservation(cafeId, dto, member);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationPostRes> updateReservation(
            @RequestBody ReservationPatchReq dto,
            @PathVariable Long reservationId
    ) {
        ReservationPostRes response = reservationService.updateReservation(reservationId, dto, LocalDateTime.now(), member);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/checkout/{reservationId}")
    public ResponseEntity<Void> checkout(
            @PathVariable Long reservationId
    ) {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        reservationService.checkout(reservationId, now, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @RequestBody ReservationPostReq dto,
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(reservationId, LocalDateTime.now(), member);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/availableCounts/{cafeId}")
    public ResponseEntity<AvailableSeatsCount> getAvailableSeatsCount(
            @PathVariable Long cafeId,
            @RequestBody SeatCountReq req
            ) {

        AvailableSeatsCount res = reservationService.getAvailableSeatsCount(cafeId, req.reservationTime().startTime(), req.reservationTime().endTime());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/availableTimeSlots/{cafeId}")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(
            @PathVariable Long cafeId,
            @RequestBody TimeSlotsReq req
            ) {
        List<TimeSlot> res = reservationService.getAvailableTimeSlots(cafeId, req);
        return ResponseEntity.ok(res);
    }

    //예약 디테일 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationDetailRes> getReservationDetail(
            @PathVariable Long reservationId
    ) {
        ReservationDetailRes res = reservationService.getReservationDetail(reservationId, member);
        return ResponseEntity.ok(res);
    }

    //특정 사용자의 예약 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(
            @PathVariable Long userId,
            @RequestParam(required = false) Long cursorId
    ) {
        List<UserReservationRes> reservations = reservationService.getUserReservations(userId, cursorId);
        return ResponseEntity.ok(reservations);
    }


    //특정 카페의 예약 조회 (날짜 기준 필터링)
    @GetMapping("/cafe/{cafeId}")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
            @PathVariable Long cafeId,
            @RequestParam LocalDate date)  {
        List<CafeReservationRes> res = reservationService.getCafeReservations(cafeId, date);
        return ResponseEntity.ok(res);
    }
}
