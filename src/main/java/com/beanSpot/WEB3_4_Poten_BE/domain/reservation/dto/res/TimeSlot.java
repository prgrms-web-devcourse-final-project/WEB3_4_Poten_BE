package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.res;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimeSlot
 {
    private LocalDateTime start;
    private LocalDateTime end;
}
