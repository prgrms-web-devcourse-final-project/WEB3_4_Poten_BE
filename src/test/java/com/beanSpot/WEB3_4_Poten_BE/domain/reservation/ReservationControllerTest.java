package com.beanSpot.WEB3_4_Poten_BE.domain.reservation;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.controller.ReservationController;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.ReservationPostReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res.ReservationPostRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.ReservationStatus;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReservationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private ReservationPostReq validReservationRequest;

    @BeforeEach
    void setUp() {
        validReservationRequest = new ReservationPostReq();
        validReservationRequest.setUserId(1L);
        validReservationRequest.setCafeId(1L);
        validReservationRequest.setSeatId(1L);
        validReservationRequest.setStartTime(LocalDateTime.now().plusHours(1));
        validReservationRequest.setEndTime(LocalDateTime.now().plusHours(2));
        validReservationRequest.setPaymentId(1L);
    }

    @Test
    @DisplayName("성공적인 예약 생성 테스트")
    void createReservation_Success() {
        // When
        ResponseEntity<ReservationPostRes> response = restTemplate.postForEntity(
                "/reservations",
                validReservationRequest,
                ReservationPostRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getReservationId()).isNotNull();
        assertThat(response.getBody().getStatus()).isNotNull();
    }

    @Test
    @DisplayName("잘못된 시간 범위로 예약 실패 테스트")
    void createReservation_InvalidTimeRange_Failure() {
        // Given
        ReservationPostReq invalidTimeRequest = new ReservationPostReq();
        invalidTimeRequest.setUserId(1L);
        invalidTimeRequest.setCafeId(1L);
        invalidTimeRequest.setSeatId(1L);
        invalidTimeRequest.setStartTime(LocalDateTime.now().plusHours(2));
        invalidTimeRequest.setEndTime(LocalDateTime.now().plusHours(1));
        invalidTimeRequest.setPaymentId(1L);

        // When
        ResponseEntity<ReservationPostRes> response = restTemplate.postForEntity(
                "/reservations",
                invalidTimeRequest,
                ReservationPostRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("필수 필드 누락으로 예약 실패 테스트")
    void createReservation_MissingFields_Failure() {
        // Given
        ReservationPostReq incompleteRequest = new ReservationPostReq();
        incompleteRequest.setUserId(1L);
        // 다른 필수 필드 누락

        // When
        ResponseEntity<ReservationPostRes> response = restTemplate.postForEntity(
                "/reservations",
                incompleteRequest,
                ReservationPostRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("과거 시간으로 예약 실패 테스트")
    void createReservation_PastTime_Failure() {
        // Given
        ReservationPostReq pastTimeRequest = new ReservationPostReq();
        pastTimeRequest.setUserId(1L);
        pastTimeRequest.setCafeId(1L);
        pastTimeRequest.setSeatId(1L);
        pastTimeRequest.setStartTime(LocalDateTime.now().minusHours(1));
        pastTimeRequest.setEndTime(LocalDateTime.now().plusHours(1));
        pastTimeRequest.setPaymentId(1L);

        // When
        ResponseEntity<ReservationPostRes> response = restTemplate.postForEntity(
                "/reservations",
                pastTimeRequest,
                ReservationPostRes.class
        );

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}

