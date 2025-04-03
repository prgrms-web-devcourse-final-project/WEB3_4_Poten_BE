package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto;

import lombok.Builder;

@Builder
public record FavoriteCafeRes(
        Long cafeId,
        String name,
        String address,
        String image
) {
}