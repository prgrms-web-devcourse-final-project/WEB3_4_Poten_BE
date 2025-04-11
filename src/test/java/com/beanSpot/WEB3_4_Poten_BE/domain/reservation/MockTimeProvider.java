package com.beanSpot.WEB3_4_Poten_BE.domain.reservation;

import com.beanSpot.WEB3_4_Poten_BE.global.util.timeProvider.TimeProvider;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class MockTimeProvider implements TimeProvider {

    private LocalDateTime currentTime;

    public MockTimeProvider() {
        this.currentTime = null;
    }

    public MockTimeProvider(LocalDateTime fixedTime) {
        this.currentTime = fixedTime;
    }

    @Override
    public LocalDateTime now() {
        return currentTime;
    }

    @Override
    public LocalDateTime nowMinute() {
        return currentTime.truncatedTo(ChronoUnit.MINUTES);
    }

    public void setCurrentTime(LocalDateTime newTime) {
        this.currentTime = newTime;
    }
}