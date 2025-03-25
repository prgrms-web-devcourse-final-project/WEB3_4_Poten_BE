package com.beanSpot.WEB3_4_Poten_BE.domain.reservation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //같은 카페내에서는 유니크 해야함
    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private int capacity;

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(nullable = false)
    //private Cafe cafe; // 좌석이 속한 카페
}
