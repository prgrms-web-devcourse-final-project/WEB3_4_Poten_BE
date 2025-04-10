package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.common.entity.Reservable;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.TimeSlotsReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.*;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.beanSpot.WEB3_4_Poten_BE.domain.reservation.util.ReservationUtil.getAvailableTimeSlotsHelper;
import static com.beanSpot.WEB3_4_Poten_BE.domain.reservation.util.ReservationUtil.getMaxOccupiedSeatsCount;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final CafeRepository cafeRepository;
    //예약 생성
    @Transactional
    public ReservationPostRes createReservation(Long cafeId, ReservationPostReq dto, Member member) {

        //카페 조회
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ServiceException(400, "존재하지 않는 카페 Id 입니다."));

        // 예약 가능 여부 확인
        List<Reservable> overlappingReservations = reservationRepository.getOverlappingReservationsWithLock(
                cafeId,
                dto.getReservationTime().startTime(),
                dto.getReservationTime().endTime(),
                null);

        int maxOccupiedSeatCount = getMaxOccupiedSeatsCount(
                overlappingReservations,
                dto.getReservationTime().startTime(),
                dto.getReservationTime().endTime());

        if (maxOccupiedSeatCount + dto.getPartySize() > cafe.getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        Reservation reservation = Reservation.builder()
                .cafe(cafe)
                .member(member)
                .startTime(dto.getReservationTime().startTime())
                .endTime(dto.getReservationTime().endTime())
                .status(ReservationStatus.CONFIRMED)
                .partySize(dto.getPartySize())
                .build();
        reservationRepository.save(reservation);

        return ReservationPostRes.from(reservation);
    }

    //예약 수정
    @Transactional
    public ReservationPostRes updateReservation(Long reservationId, ReservationPatchReq dto, LocalDateTime now, Member member) {

        //예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ServiceException(400, "해당 예약을 찾을 수 없습니다."));

        //원래 예약시간 0분전 변경 가능하게 체크
        if (!reservation.isModifiable(now, 0, member)) {
            throw new ServiceException(400, "변경이 불가능합니다. 점주님께 문의하세요.");
        }

        // 예약 가능 여부 확인
        List<Reservable> overlappingReservations = reservationRepository.getOverlappingReservationsWithLock(
                reservation.getCafe().getCafeId(),
                dto.getReservationTime().startTime(),
                dto.getReservationTime().endTime(),
                reservationId);

        int maxOccupiedSeatCount = getMaxOccupiedSeatsCount(
                overlappingReservations,
                dto.getReservationTime().startTime(),
                dto.getReservationTime().endTime());

        if (maxOccupiedSeatCount + dto.getPartySize() > reservation.getCafe().getCapacity()) {
            throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
        }

        // 예약 정보 업데이트
        reservation.update(dto.getReservationTime().startTime(), dto.getReservationTime().endTime(), dto.getPartySize());

        return ReservationPostRes.from(reservation);
    }

    //사용중간에 체크아웃 하는 메소드
    @Transactional
    public void checkout(long reservationId, LocalDateTime checkoutTime, Member member) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));


        if (!reservation.isCheckoutTimeValid(checkoutTime, member)) {
            throw new ServiceException(400, "잘못된 체크아웃 시간대 입니다");
        }

        reservation.updateReservationTime(reservation.getStartTime(), checkoutTime);
    }

    //예약 취소
    @Transactional
    public void cancelReservation(long reservationId, LocalDateTime now, Member member) {
        // 예약 조회
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        //시작시간 0분전 취소 불가능
        if (!reservation.isModifiable(now, 0, member)) {
            throw new ServiceException(400, "취소가 불가능합니다. 점주님께 문의하세요.");
        }

        reservation.updateStatus(ReservationStatus.CANCELLED);
    }

    //사용가능한 시간대 조회
    @Transactional(readOnly = true)
    public List<TimeSlot> getAvailableTimeSlots(long cafeId, TimeSlotsReq req) {

        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ServiceException(400, "존재하지 않는 카페입니다"));

        List<Reservable> overlappingReservations =
                reservationRepository.getOverlappingReservations(cafeId, req.startTime(), req.endTime(), null);

        return getAvailableTimeSlotsHelper(
                overlappingReservations,
                cafe.getCapacity(),
                req.partySize(),
                req.startTime(),
                req.endTime()
        );
    }

    // 사용중인 좌석수 조회
    @Transactional(readOnly = true)
    public AvailableSeatsCount getAvailableSeatsCount(long cafeId, LocalDateTime start, LocalDateTime end) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ServiceException(400, "존재하지 않는 카페입니다"));

        List<Reservable> overlappingReservations = reservationRepository.getOverlappingReservations(cafeId, start, end, null);

        int maxOccupiedSeatCount = getMaxOccupiedSeatsCount(overlappingReservations, start, end);
        int availableSeats =  cafe.getCapacity() - maxOccupiedSeatCount;
        return new AvailableSeatsCount(availableSeats, cafe.getCapacity());
    }

    // 예약 상세 조회
    @Transactional(readOnly = true)
    public ReservationDetailRes getReservationDetail(Long reservationId, Member member) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 예약을 찾을 수 없습니다."));

        if (!reservation.isOwner(member)) {
            throw new ServiceException(400, "해당 예약조회 권한이 없습니다");
        }

        return ReservationDetailRes.from(reservation);
    }

    //특정 사용자의 예약 목록 조회
    @Transactional(readOnly = true)
    public List<UserReservationRes> getUserReservations(Long userId, Long cursorId) {
        List<Reservation> reservations = reservationRepository.findReservationsByMemberId(userId, cursorId,10);

        return reservations.stream()
                .map(UserReservationRes::from)
                .collect(Collectors.toList());
    }

    //특정 카페의 예약 조회 (날짜 기준 필터링)
    @Transactional(readOnly = true)
    public List<CafeReservationRes> getCafeReservations(Long cafeId, LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime startOfNextDay = startOfDay.plusDays(1);

        List<Reservation> reservations = reservationRepository.findByCafeIdAndDate(cafeId, startOfDay, startOfNextDay);

        //TODO: 멤버가 카페의 멤버와 다른면 에러 던지게 하기

        return reservations.stream()
                .map(CafeReservationRes::from)
                .toList();
    }

//    //예약가능 시간대들을 구하는 메소드
//    private List<TimeSlot> getAvailableTimeSlotsHelper(List<Reservation> overlappingReservations, int capacity, int partySize, LocalDateTime start, LocalDateTime end) {
//
//        int remainingCapacity = capacity - partySize;
//
//        // 인원수가 capacity 보다 더 큰경우
//        if (remainingCapacity < 0) {
//            return Collections.emptyList();
//        }
//
//        // 만약 예약이 없다면 전체시간 반환
//        if (overlappingReservations.isEmpty()) {
//            return Collections.singletonList(new TimeSlot(start, end));
//        }
//
//        // 시간 이벤트 생성
//        List<TimeWithPartySize> events = new ArrayList<>();
//
//        for (Reservation reservation : overlappingReservations) {
//            events.add(new TimeWithPartySize(reservation.getStartTime(), reservation.getPartySize()));
//            events.add(new TimeWithPartySize(reservation.getEndTime(), -1 * reservation.getPartySize()));
//        }
//
//        events.sort(Comparator.comparing(TimeWithPartySize::time)
//                .thenComparingInt(TimeWithPartySize::partySize));
//
//        List<TimeSlot> availableSlots = new ArrayList<>();
//        int currentOccupancy = 0;
//        LocalDateTime availableStart = events.getFirst().time();
//        boolean isCurrentlyAvailable = true;
//
//
//        for (TimeWithPartySize event : events) {
//            // 현 좌석 계산
//            currentOccupancy += event.partySize();
//
//
//            boolean willBeAvailable = currentOccupancy <= remainingCapacity;
//
//            // 자리가 있다가 더이상 없는 순간
//            if (isCurrentlyAvailable && !willBeAvailable) {
//                availableSlots.add(new TimeSlot(
//                        availableStart,
//                        event.time()
//                ));
//            }
//
//            // 자리가 없다가 생긴 순간
//            else if (!isCurrentlyAvailable && willBeAvailable) {
//                availableStart = event.time();
//            }
//
//            isCurrentlyAvailable = willBeAvailable;
//        }
//
//        // 만약 마지막 상태가 예약가능이면
//        if (isCurrentlyAvailable && availableStart.isBefore(end)) {
//            availableSlots.add(new TimeSlot(
//                    //만약 start 전에 시작하면 시작을 start 로 변경
//                    availableStart.isBefore(start) ? start : availableStart,
//                    end
//            ));
//        }
//
//        List<TimeSlot> filtered = new ArrayList<>();
//
//        //time slot이 범위 밖이거나 걸쳐있는경우 필터링해주거나 값조정하는 로직
//        for (TimeSlot slot : availableSlots) {
//            if (slot.getEnd().isBefore(start) || slot.getStart().isAfter(end)) continue;
//
//            slot.setStart(slot.getStart().isBefore(start) ? start : slot.getStart());
//            slot.setEnd(slot.getEnd().isAfter(end) ? end : slot.getEnd());
//
//            if (slot.getStart().equals(slot.getEnd())) continue;
//
//            filtered.add(slot);
//        }
//
//        return filtered;
//    }
//
//    private int getMaxOccupiedSeatsCount(List<Reservation> overlappingReservations) {
//        // 시작/종료 시간을 하나의 이벤트 리스트로 통합
//        List<TimeWithPartySize> events = new ArrayList<>();
//
//        for (Reservation reservation : overlappingReservations) {
//            // 시작 이벤트는 좌석 추가
//            events.add(new TimeWithPartySize(reservation.getStartTime(), reservation.getPartySize()));
//            // 종료 이벤트는 좌석 감소
//            events.add(new TimeWithPartySize(reservation.getEndTime(), -reservation.getPartySize()));
//        }
//
//        // 시간순으로 정렬, 같은 시간이면 종료 이벤트가 먼저 오도록 정렬
//        events.sort(Comparator.comparing(TimeWithPartySize::time)
//                .thenComparingInt(TimeWithPartySize::partySize));
//
//        int currentSeats = 0;
//        int maxSeats = 0;
//
//        for (TimeWithPartySize event : events) {
//            currentSeats += event.partySize();
//            maxSeats = Math.max(maxSeats, currentSeats);
//        }
//
//        return maxSeats;
//    }
}
