package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.validation.validator;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.dto.req.TimePeriodReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.validation.annotation.ValidTimePeriod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimePeriodValidator implements ConstraintValidator<ValidTimePeriod, TimePeriodReq> {

    @Override
    public boolean isValid(TimePeriodReq dto, ConstraintValidatorContext context) {
        if (dto == null) {
            customMessageForViolation(context, "시간 기간 요청이 null일 수 없습니다");
            return false;
        }

        LocalDateTime startTime = dto.startTime();
        LocalDateTime endTime = dto.endTime();

        if (startTime.isAfter(endTime)) {
            customMessageForViolation(context, "시작 시간은 종료 시간보다 늦을 수 없습니다");
            return false;
        }

        if (Duration.between(startTime, endTime).toMinutes() < 30) {
            customMessageForViolation(context, "시간 기간은 최소 30분 이상이어야 합니다");
            return false;
        }

        return true;
    }

    private void customMessageForViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
