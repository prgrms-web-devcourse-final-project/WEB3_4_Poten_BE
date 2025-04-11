package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service.ReservationService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;


@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController implements ReservationApi{
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


    // 예약 생성 API
    @PostMapping("/{cafeId}")
    public ResponseEntity<ReservationPostRes> createReservation(
            @RequestParam Long cafeId,
            @Valid @RequestBody ReservationPostReq dto
    ) {
        //TODO: 추후 리팩토링 하기
        ReservationPostRes response = reservationService.createReservation(cafeId, dto, member);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity<ReservationPostRes> updateReservation(
            @Valid @RequestBody ReservationPatchReq dto,
            @PathVariable Long reservationId
    ) {
        ReservationPostRes response = reservationService.updateReservation(reservationId, dto, member);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/checkout/{reservationId}")
    public ResponseEntity<Void> checkout(
            @PathVariable Long reservationId
    ) {
        reservationService.checkout(reservationId, member);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(
            @Valid @RequestBody ReservationPostReq dto,
            @PathVariable Long reservationId
    ) {
        reservationService.cancelReservation(reservationId, member);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/availableCounts/{cafeId}")
    public ResponseEntity<AvailableSeatsCount> getAvailableSeatsCount(
            @PathVariable Long cafeId,
            @Valid @ModelAttribute SeatCountReq req
    ) {
        AvailableSeatsCount res = reservationService.getAvailableSeatsCount(cafeId, req.startTime(), req.endTime());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/availableTimeSlots/{cafeId}")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(
            @PathVariable Long cafeId,
            @Valid @ModelAttribute TimeSlotsReq req
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
