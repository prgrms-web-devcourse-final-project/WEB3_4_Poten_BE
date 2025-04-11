package com.beanSpot.WEB3_4_Poten_BE.global.util.timeProvider;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class RealTimeProvider implements TimeProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }

    public LocalDateTime nowMinute() {
        return now().truncatedTo(ChronoUnit.MINUTES);
    }
}
