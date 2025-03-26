package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req;

import lombok.Getter;
import lombok.Setter;

public record ReservationDeleteReq(Long reservationId, Long userId) {}
