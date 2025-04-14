package com.beanSpot.WEB3_4_Poten_BE.global.util.timeProvider;

import java.time.LocalDateTime;

public interface TimeProvider {
    LocalDateTime now();
    LocalDateTime nowMinute();
}