package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.SeatCountReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.TimeSlotsReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.List;

public interface ReservationApi {

    @Operation(summary = "예약 생성")
    public ResponseEntity<ReservationPostRes> createReservation(
            @PathVariable Long cafeId,
            ReservationPostReq dto,
            SecurityUser user
    );

    @Operation(summary = "예약 변경")
    public ResponseEntity<ReservationPostRes> updateReservation(
            ReservationPatchReq dto,
            Long reservationId,
            SecurityUser user
    );

    @Operation(summary = "카페 사용 중간에 체크아웃")
    public ResponseEntity<Void> checkout(
            Long reservationId,
            SecurityUser user
    );

    @Operation(summary = "예약취소")
    public ResponseEntity<Void> deleteReservation(
            Long reservationId,
            SecurityUser user
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
            SecurityUser user
    );

    @Operation(summary = "유저의 예약조회", description = "reservationId 기반으로 커서페이징 하는 기능입니다")
    public ResponseEntity<List<UserReservationRes>> getUserReservations(
            Long cursorId,
            SecurityUser user
    );


    @Operation(summary = "점주의 특정 한 카페의 예약 조회 (날짜 기준 필터링)")
    public ResponseEntity<List<CafeReservationRes>> getCafeReservations(
            Long cafeId,
            LocalDate date,
            SecurityUser user
    );

    @Operation(summary = "점주의 모든 카페 예약 조회", description = "점주가 소유한 모든 카페들의 예약을 날짜 기준으로 조회합니다.")
    public ResponseEntity<List<CafeReservationRes>> getOwnerReservations(
        LocalDate date,
        SecurityUser user
    );
}
