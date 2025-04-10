package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.redis;

import com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity.Reservation;
import com.beanSpot.WEB3_4_Poten_BE.global.config.RedissonConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisReservationService {
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;
    private final RedissonClient redissonClient;
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();

    public void attemptReservationHold(RedisReservation reservation, Long cafeId, int capacity) {
        LocalDate date = reservation.getStartTime().toLocalDate();
        String key = buildKey(cafeId, date);
        RLock lock = redissonClient.getLock("lock:" + key);
        boolean locked = false;

        try {
            locked = lock.tryLock(3, 5, TimeUnit.SECONDS); // max wait 3s, lease time 5s
            if (!locked) {
                throw new IllegalStateException("잠시 후 다시 시도해주세요.");
            }

            // 1. Redis에서 유효 예약 가져오기
            List<RedisReservation> reservations = getReservations(cafeId, date);

            // 2. 좌석 수 계산
            int occupied = getMaxOccupiedSeatsCount(reservations);
            if (occupied + reservation.getPartySize() > capacity) {
                throw new IllegalStateException("선택한 예약시간에 빈좌석이 없습니다.");
            }

            // 3. 저장
            saveReservation(cafeId, date, reservation);

        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 실패", e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    private void saveReservation(Long cafeId, LocalDate date, RedisReservation reservation) {
        String key = buildKey(cafeId, date);

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        //만료시간 구하는거 리팩토링 하기
        LocalDateTime nowPlus5 = LocalDateTime.now().plusMinutes(5);
        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime expirationDate = nowPlus5.isBefore(startTime) ? nowPlus5 : startTime;

        try {
            String value = objectMapper.writeValueAsString(reservation);
            long expirationScore = expirationDate
                    .atZone(ZONE_ID)
                    .toInstant()
                    .toEpochMilli();

            zSetOps.add(key, value, expirationScore);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Redis 직렬화 실패", e);
        }
    }


    public List<RedisReservation> getReservations(Long cafeId, LocalDate date) {
        String key = buildKey(cafeId, date);
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        long nowScore = Instant.now().toEpochMilli();
        Set<String> rawValues = zSetOps.rangeByScore(key, nowScore, Double.MAX_VALUE);
        if (rawValues == null) return Collections.emptyList();

        return rawValues.stream()
                .map(this::deserialize)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }



    private RedisReservation deserialize(String json) {
        try {
            return objectMapper.readValue(json, RedisReservation.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String buildKey(Long cafeId, LocalDate date) {
        return String.format("reservation:%d:%s", cafeId, date);
    }
}
