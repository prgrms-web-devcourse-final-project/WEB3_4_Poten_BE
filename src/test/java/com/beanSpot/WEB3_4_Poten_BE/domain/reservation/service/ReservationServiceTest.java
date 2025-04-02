package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPatchReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.TimeSlotsReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.AvailableSeatsCount;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.TimeSlot;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DataJpaTest
@Transactional
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CafeRepository cafeRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ReservationService reservationService;

    private Cafe cafe;
    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;
    private Reservation reservation4;

    @BeforeEach
    void setUp() {
        // 테스트용 Cafe 및 Reservation 저장
        cafe = Cafe.builder()
                .name("cafe1")
                .image("img1")
                .address("seoul")
                .capacity(5)
                .disabled(false)
                .latitude(1.0)
                .longitude(2.0)
                .phone("010-1234-5678")
                .description("nice place")
                .createdAt(LocalDateTime.of(2025, 1, 1, 0, 0))
                .build();
        cafe = cafeRepository.save(cafe);

        reservationService = new ReservationService(reservationRepository, cafeRepository);

        reservation1 = Reservation.builder()
                .cafe(cafe)
                .startTime(LocalDateTime.of(2025, 1, 1, 12, 0))
                .endTime(LocalDateTime.of(2025, 1, 1, 13, 0))
                .partySize(2)
                .status(ReservationStatus.CONFIRMED)
                .build();

        reservation2 = Reservation.builder()
                .cafe(cafe)
                .startTime(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 1, 1, 12, 0))
                .status(ReservationStatus.CONFIRMED)
                .partySize(4)
                .build();
        reservation2 = reservationRepository.save(reservation2);

        reservation3 = Reservation.builder()
                .cafe(cafe)
                .startTime(LocalDateTime.of(2025, 1, 1, 12, 30))
                .endTime(LocalDateTime.of(2025, 1, 1, 14, 0))
                .status(ReservationStatus.CONFIRMED)
                .partySize(3)
                .build();
        reservation3 = reservationRepository.save(reservation3);

        reservation4 = Reservation.builder()
                .cafe(cafe)
                .startTime(LocalDateTime.of(2025, 1, 1, 12, 0))
                .endTime(LocalDateTime.of(2025, 1, 1, 13, 0))
                .status(ReservationStatus.CANCELLED)
                .partySize(5)
                .build();
        reservation4 = reservationRepository.save(reservation4);
    }

    @Test
    @DisplayName("예약 성공 테스트")
    void t1() {
        // Given
        ReservationPostReq request = ReservationPostReq.builder()
                .startTime(reservation1.getStartTime())
                .endTime(reservation1.getEndTime())
                .partySize(reservation1.getPartySize())
                .build();

        // When
        ReservationPostRes response = reservationService.createReservation(cafe.getCafeId(), request);

        // Then
        assertNotNull(response);
        assertEquals(request.getStartTime(), response.getStartTime());
        assertEquals(request.getEndTime(), response.getEndTime());
        assertEquals(request.getEndTime(), response.getEndTime());
        assertEquals(request.getPartySize(), response.getPartySize());

        // DB에서 실제 데이터 확인
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(4);
    }

    @Test
    @DisplayName("예약 실패 테스트")
    void t2() {
        // Given
        reservation1.updateReservationTime(LocalDateTime.of(2025, 1, 1, 11, 10), reservation1.getEndTime());

        ReservationPostReq request = ReservationPostReq.builder()
                .startTime(reservation1.getStartTime())
                .endTime(reservation1.getEndTime())
                .partySize(reservation1.getPartySize())
                .build();

        // When & Then
        assertThrows(RuntimeException.class, () -> reservationService.createReservation(cafe.getCafeId(), request));

        // DB에서 실제 데이터 확인
        List<Reservation> reservations = reservationRepository.findAll();
        assertThat(reservations).hasSize(3);
    }

    @Test
    @DisplayName("예약 수정")
    void t3() {
        // Given
        reservation1 = reservationRepository.save(reservation1);

        ReservationPatchReq request = ReservationPatchReq
                .builder()
                .startTime(LocalDateTime.of(2025, 1, 1, 12, 10))
                .endTime(LocalDateTime.of(2025, 1, 1, 12, 30))
                .partySize(5)
                .build();

        // When
        ReservationPostRes response = reservationService.updateReservation(
                reservation1.getId(),
                request,
                LocalDateTime.of(2025, 1, 1, 10, 0));

        // Then
        assertNotNull(response);
        assertEquals(request.getStartTime(), response.getStartTime());
        assertEquals(request.getEndTime(), response.getEndTime());
        assertEquals(5, response.getPartySize());


        // DB에서 실제 데이터 확인
        Reservation updatedReservation = reservationRepository.findById(reservation1.getId()).orElseThrow();
        assertEquals(request.getStartTime(), updatedReservation.getStartTime());
        assertEquals(request.getEndTime(), updatedReservation.getEndTime());
        assertEquals(5, updatedReservation.getPartySize());
    }

    @Test
    @DisplayName("예약 삭제")
    void t4() {
        //Given
        reservation1 = reservationRepository.save(reservation1);

        // When
        reservationService.cancelReservation(reservation1.getId(), LocalDateTime.of(2025, 1, 1, 10, 0));

        // Then
        Reservation canceledReservation = reservationRepository.findById(reservation1.getId()).orElseThrow();
        assertEquals(ReservationStatus.CANCELLED, canceledReservation.getStatus());
    }

    @Test
    @DisplayName("예약 수정 실패")
    void t5() {
        // Given
        reservation1 = reservationRepository.save(reservation1);

        ReservationPatchReq request = ReservationPatchReq
                .builder()
                .startTime(LocalDateTime.of(2025, 1, 1, 11, 59))
                .endTime(reservation1.getEndTime())
                .partySize(reservation1.getPartySize())
                .build();

        // Then
        assertThrows(RuntimeException.class, () -> reservationService.updateReservation(reservation1.getId(), request, LocalDateTime.of(2025, 1, 1, 10, 0)));
    }

    @Test
    @DisplayName("사용 가능한 시간대 조회")
    void t6() {
        TimeSlotsReq req = TimeSlotsReq.builder()
                .startTime(LocalDateTime.of(2025, 1, 1, 10, 0))
                .endTime(LocalDateTime.of(2025, 1, 1, 14, 0))
                .partySize(reservation1.getPartySize())
                .build();

        List<TimeSlot> res = reservationService.getAvailableTimeSlots(cafe.getCafeId(), req);
        assertEquals(LocalDateTime.of(2025, 1, 1, 12, 0), res.getFirst().getStart());
        assertEquals(LocalDateTime.of(2025, 1, 1, 14, 0), res.getFirst().getEnd());

    }

    @Test
    @DisplayName("잔여좌석 조회")
    void t7() {
        reservation1.update(reservation1.getStartTime(), reservation1.getEndTime(), 1);
        reservationRepository.save(reservation1);

        AvailableSeatsCount res = reservationService.getAvailableSeatsCount(
                cafe.getCafeId(),
                LocalDateTime.of(2025, 1, 1, 9, 0),
                LocalDateTime.of(2025, 1, 1, 14, 0));

        assertEquals(1, res.availableSeats());
        assertEquals(5, res.totalSeats());

    }
}
