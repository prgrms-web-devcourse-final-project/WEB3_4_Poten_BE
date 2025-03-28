package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //같은 카페내에서는 유니크 해야함
    @Column(nullable = false)
    private String seatNumber;

    //capacity 가 2이상이면 공유 좌석
    @Column(nullable = false)
    private int capacity;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Cafe cafe; // 좌석이 속한 카페
}
