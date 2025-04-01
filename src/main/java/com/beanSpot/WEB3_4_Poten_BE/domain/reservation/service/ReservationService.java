package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationCheckoutReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.vo.TimeWithPartySize;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CafeRepository cafeRepository;
    //TODO: 시간관련 엣지케이스 고려하기
    // ✅ 1. 예약 생성
    @Transactional
    public ReservationPostRes createReservation(ReservationPostReq dto) {

        //카페 조회
        Cafe cafe = cafeRepository.findById(dto.getCafeId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카페 Id 입니다."));

        // 예약 가능 여부 확인
        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservationsWithLock(
                dto.getCafeId(),
                dto.getStartTime(),
                dto.getEndTime(),
                null);

        if (getMaxOccupiedSeatsCount(overlappingReservations) + dto.getPartySize() > cafe.getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        Reservation reservation = Reservation.builder()
                .cafe(cafe)
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();
        reservationRepository.save(reservation);

        return ReservationPostRes.from(reservation);
    }

    // ✅ 2. 예약 수정
    @Transactional
    public ReservationPostRes updateReservation(Long reservationId, ReservationPatchReq dto, LocalDateTime now) {

        //예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));

        //원래 예약시간 0분전 변경 가능하게 체크
        if (!reservation.isModifiable(now, 0)) {
            throw new RuntimeException("변경이 불가능합니다. 점주님께 문의하세요.");
        }

        // 예약 가능 여부 확인
        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservationsWithLock(
                reservation.getCafe().getCafeId(),
                dto.getStartTime(),
                dto.getEndTime(),
                reservationId);

        if (getMaxOccupiedSeatsCount(overlappingReservations) + dto.getPartySize() > reservation.getCafe().getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        // 예약 정보 업데이트
        reservation.update(dto.getStartTime(), dto.getEndTime());

        return ReservationPostRes.from(reservation);
    }

    //사용중간에 체크아웃 하는 메소드
    @Transactional
    public void checkout(long reservationId, ReservationCheckoutReq req) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));


        if (!reservation.isCheckoutTimeValid(req.checkoutTime())) {
            throw new RuntimeException("잘못된 체크아웃 시간대 입니다");
        }

        reservation.update(reservation.getStartTime(), req.checkoutTime());
    }

    // ✅ 3. 예약 취소
    @Transactional
    public void cancelReservation(long reservationId, LocalDateTime now) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        //시작시간 0분전 취소 불가능
        if (!reservation.isModifiable(now, 0)) {
            throw new RuntimeException("취소가 불가능합니다. 점주님께 문의하세요.");
        }

        reservation.cancelReservation();
    }

    //TODO: 사용 가능한시간대 알려주는 기능

    // 사용중인 좌석수 조회
    @Transactional(readOnly = true)
    public int getAvailableSeatsCount(long cafeId, LocalDateTime start, LocalDateTime end) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 카페입니다"));

        List<Reservation> overlappingReservations = reservationRepository.getOverlappingReservations(cafeId, start, end, null);
        return cafe.getCapacity() - getMaxOccupiedSeatsCount(overlappingReservations);
    }

    // ✅ 4. 예약 상세 조회
    @Transactional(readOnly = true)
    public ReservationDetailRes getReservationDetail(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        return ReservationDetailRes.from(reservation);
    }

    // ✅ 5. 특정 사용자의 예약 목록 조회
    @Transactional(readOnly = true)
    public List<UserReservationRes> getUserReservations(Long userId) {
        //TODO: 멤버 추가시 수정 주석처리된거 사용하기
        //List<Reservation> reservations = reservationRepository.findByUserIdOrderByStartTimeDesc(userId);
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(new Reservation());

        return reservations.stream()
                .map(UserReservationRes::from)
                .collect(Collectors.toList());
    }

    // ✅ 6. 특정 카페의 예약 조회 (날짜 기준 필터링)
    @Transactional(readOnly = true)
    public List<CafeReservationRes> getCafeReservations(Long cafeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(23, 59, 59);

        List<Reservation> reservations = reservationRepository.findByCafeIdAndDate(cafeId, startOfDay, endOfDay);
        return reservations.stream()
                .map(CafeReservationRes::from)
                .toList();
    }

    //예약가능 시간대들을 구하는 메소드
    private List<TimeSlot> getAvailableTimeSlots(List<Reservation> overlappingReservations, int capacity, int partySize, LocalDateTime start, LocalDateTime end) {
        capacity = capacity - partySize;

        List<TimeSlot> timeSlots = new ArrayList<>();
        List<TimeWithPartySize> startTimes = new ArrayList<>();
        List<TimeWithPartySize> endTimes = new ArrayList<>();

        for (Reservation reservation : overlappingReservations) {
            startTimes.add(new TimeWithPartySize(reservation.getStartTime(), reservation.getPartySize()));
            endTimes.add(new TimeWithPartySize(reservation.getEndTime(), reservation.getPartySize()));
        }

        // startTimes 기준으로 정렬 (시간만 비교)
        startTimes.sort(Comparator.comparing(TimeWithPartySize::time));
        // endTimes도 동일하게 정렬
        endTimes.sort(Comparator.comparing(TimeWithPartySize::time));

        int startTimesIndex = 0;
        int endTimesIndex = 0;
        //현재 겹치는 예약수
        int count = 0;
        //쵀대로 겹치는 예약수
        int maxCount = 0;
        //현재 예약가능한지
        boolean available = false;

        while (startTimesIndex < startTimes.size() || endTimesIndex < endTimes.size()) {
            LocalDateTime curStartTime = startTimesIndex < startTimes.size()
                    ? startTimes.get(startTimesIndex).time() : LocalDateTime.MAX;

            LocalDateTime curEndTime = endTimesIndex < endTimes.size()
                    ? endTimes.get(endTimesIndex).time() : LocalDateTime.MAX;

            int curStartPartySize = startTimesIndex < startTimes.size()
                    ? startTimes.get(startTimesIndex).partySize() : 0;

            int curEndPartySize = endTimesIndex < endTimes.size()
                    ? endTimes.get(endTimesIndex).partySize() : 0;

            //시작과 끝중 더먼저오는거. 만약 시작이면 겹치는 예약 증가, 끝나는거면 겹치는 예약 감소
            if (curStartTime.isBefore(curEndTime)) {
                ++startTimesIndex;
                count += curStartPartySize;
            } else {
                ++endTimesIndex;
                count -= curEndPartySize;
            }

            maxCount = Math.max(maxCount, count);


            if (available && count >= capacity) {
                available = false;
                TimeSlot timeSlot = timeSlots.getLast();
                timeSlot.setEnd(curStartTime);
            } else if (!available && count < capacity) {
                available = true;
                timeSlots.add(new TimeSlot(curEndTime, null));
            }
        }

        if (maxCount < capacity) {
            return Collections.singletonList(new TimeSlot(start, end));
        }

        List<TimeSlot> filtered = new ArrayList<>();
        //timeSlot 필터링작업
        for (TimeSlot slot : timeSlots) {
            if (slot.getEnd().isBefore(start) || slot.getStart().isAfter(end)) continue;

            slot.setStart(slot.getStart().isBefore(start) ? start : slot.getStart());
            slot.setEnd(slot.getEnd() == null || slot.getEnd().isAfter(end) ? end : slot.getEnd());

            if (slot.getStart().equals(slot.getEnd())) continue;

            filtered.add(slot);
        }

        return filtered;
    }

    private int getMaxOccupiedSeatsCount(List<Reservation> overlappingReservations) {
        List<TimeWithPartySize> startTimes = new ArrayList<>();
        List<TimeWithPartySize> endTimes = new ArrayList<>();

        for (Reservation reservation : overlappingReservations) {
            startTimes.add(new TimeWithPartySize(reservation.getStartTime(), reservation.getPartySize()));
            endTimes.add(new TimeWithPartySize(reservation.getEndTime(), reservation.getPartySize()));
        }

        // startTimes 기준으로 정렬 (시간만 비교)
        startTimes.sort(Comparator.comparing(TimeWithPartySize::time));

        // endTimes도 동일하게 정렬
        endTimes.sort(Comparator.comparing(TimeWithPartySize::time));

        int startTimesIndex = 0;
        int endTimesIndex = 0;
        int count = 0;
        int res = 0;

        while (startTimesIndex < startTimes.size() || endTimesIndex < endTimes.size()) {
            LocalDateTime curStartTime = startTimesIndex < startTimes.size()
                    ? startTimes.get(startTimesIndex).time() : LocalDateTime.MAX;

            LocalDateTime curEndTime = endTimesIndex < endTimes.size()
                    ? endTimes.get(endTimesIndex).time() : LocalDateTime.MAX;

            int curStartPartySize = startTimesIndex < startTimes.size()
                    ? startTimes.get(startTimesIndex).partySize() : 0;

            int curEndPartySize = endTimesIndex < endTimes.size()
                    ? endTimes.get(endTimesIndex).partySize() : 0;

            if (curStartTime.isBefore(curEndTime)) {
                ++startTimesIndex;
                count += curStartPartySize;
            } else {
                ++endTimesIndex;
                count -= curEndPartySize;
            }

            res = Math.max(res, count);
        }

        return res;
    }
}
