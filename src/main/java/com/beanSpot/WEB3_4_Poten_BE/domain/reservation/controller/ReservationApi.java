package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

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

import io.swagger.v3.oas.annotations.Operation;

public interface ReservationApi {

    @Operation(summary = "예약 생성")
    public ResponseEntity<ReservationPostRes> createReservation(
        Long cafeId,
        ReservationPostReq dto,
        @AuthenticationPrincipal SecurityUser securityUser
    );

    @Operation(summary = "예약 변경")
    public ResponseEntity<ReservationPostRes> updateReservation(
        ReservationPatchReq dto,
        Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    );

    @Operation(summary = "카페 사용 중간에 체크아웃")
    public ResponseEntity<Void> checkout(
        Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    );

    @Operation(summary = "예약취소")
    public ResponseEntity<Void> deleteReservation(
        Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    );

    @Operation(summary = "사용가능 좌석수 조회")
    public ResponseEntity<AvailableSeatsCount> getAvailableSeatsCount(
        Long cafeId,
        SeatCountReq req
    );

    @Operation(summary = "사용가능 시간대 조회")
    public ResponseEntity<List<TimeSlot>> getAvailableTimeSlots(
        Long cafeId,
        TimeSlotsReq req
    );

    @Operation(summary = "예약 상세 조회")
    public ResponseEntity<ReservationDetailRes> getReservationDetail(
        Long reservationId,
        @AuthenticationPrincipal SecurityUser securityUser
    );

    @Operation(summary = "유저의 예약조회", description = "로그인한 사용자의 커서페이징 기반 예약 목록 조회")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(
        @AuthenticationPrincipal SecurityUser securityUser,
        Long cursorId
    );

    @Operation(summary = "특정 카페의 예약 조회 (날짜 기준 필터링)")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
        Long cafeId,
        LocalDate date);
}
