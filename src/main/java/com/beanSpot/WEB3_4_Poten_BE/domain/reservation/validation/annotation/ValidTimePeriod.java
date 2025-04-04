package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.validation.annotation;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.validation.validator.TimePeriodValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE}) // DTO 클래스에 적용
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TimePeriodValidator.class)
@Documented
public @interface ValidTimePeriod {
    String message() default "시작 시간은 종료 시간보다 이전이어야 하며, 최소 30분 간격이어야 합니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
