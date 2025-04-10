package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.util;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.common.entity.Reservable;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.TimeSlot;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.vo.TimeWithPartySize;

import java.time.LocalDateTime;
import java.util.*;

public class ReservationUtil {
    //예약가능 시간대들을 구하는 메소드
    public static List<TimeSlot> getAvailableTimeSlotsHelper(List<Reservable> overlappingReservations, int capacity, int partySize, LocalDateTime start, LocalDateTime end) {

        int remainingCapacity = capacity - partySize;

        // 인원수가 capacity 보다 더 큰경우
        if (remainingCapacity < 0) {
            return Collections.emptyList();
        }

        // 만약 예약이 없다면 전체시간 반환
        if (overlappingReservations.isEmpty()) {
            return Collections.singletonList(new TimeSlot(start, end));
        }

        // 시간 이벤트 생성
        List<TimeWithPartySize> events = new ArrayList<>();

        for (Reservable reservation : overlappingReservations) {
            if (reservation.getEndTime().isBefore(start) || reservation.getStartTime().isAfter(end)) {
                continue;
            }
                TimeWithPartySize.convertAndAdd(reservation, events);
        }

            events.sort(Comparator.comparing(TimeWithPartySize::time)
                    .thenComparingInt(TimeWithPartySize::partySize));

            List<TimeSlot> availableSlots = new ArrayList<>();
            int currentOccupancy = 0;
            LocalDateTime availableStart = events.getFirst().time();
            boolean isCurrentlyAvailable = true;


            for (TimeWithPartySize event : events) {
                // 현 좌석 계산
                currentOccupancy += event.partySize();


                boolean willBeAvailable = currentOccupancy <= remainingCapacity;

                // 자리가 있다가 더이상 없는 순간
                if (isCurrentlyAvailable && !willBeAvailable) {
                    availableSlots.add(new TimeSlot(
                            availableStart,
                            event.time()
                    ));
                }

                // 자리가 없다가 생긴 순간
                else if (!isCurrentlyAvailable && willBeAvailable) {
                    availableStart = event.time();
                }

                isCurrentlyAvailable = willBeAvailable;
            }

            // 만약 마지막 상태가 예약가능이면
            if (isCurrentlyAvailable && availableStart.isBefore(end)) {
                availableSlots.add(new TimeSlot(
                        //만약 start 전에 시작하면 시작을 start 로 변경
                        availableStart.isBefore(start) ? start : availableStart,
                        end
                ));
            }

            List<TimeSlot> filtered = new ArrayList<>();

            //time slot이 범위 밖이거나 걸쳐있는경우 필터링해주거나 값조정하는 로직
            for (TimeSlot slot : availableSlots) {
                if (slot.getEnd().isBefore(start) || slot.getStart().isAfter(end)) continue;

                slot.setStart(slot.getStart().isBefore(start) ? start : slot.getStart());
                slot.setEnd(slot.getEnd().isAfter(end) ? end : slot.getEnd());

                if (slot.getStart().equals(slot.getEnd())) continue;

                filtered.add(slot);
            }

            return filtered;
        }

        public static int getMaxOccupiedSeatsCount(List<Reservable> overlappingReservations, LocalDateTime start, LocalDateTime end) {
            // 시작/종료 시간을 하나의 이벤트 리스트로 통합
            List<TimeWithPartySize> events = new ArrayList<>();

            for (Reservable reservation : overlappingReservations) {
                if (reservation.getEndTime().isBefore(start) || reservation.getStartTime().isAfter(end)) {
                    continue;
                }
                TimeWithPartySize.convertAndAdd(reservation, events);
            }

            // 시간순으로 정렬, 같은 시간이면 종료 이벤트가 먼저 오도록 정렬
            events.sort(Comparator.comparing(TimeWithPartySize::time)
                    .thenComparingInt(TimeWithPartySize::partySize));

            int currentSeats = 0;
            int maxSeats = 0;

            for (TimeWithPartySize event : events) {
                currentSeats += event.partySize();
                maxSeats = Math.max(maxSeats, currentSeats);
            }

            return maxSeats;
        }
}
