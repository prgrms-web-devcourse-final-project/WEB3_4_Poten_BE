package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto;

import lombok.Builder;

/**
 * 즐겨찾기한 카페 정보 반환 DTO
 *
 * @author 김남우
 * @since 2024-04-02
 */
@Builder
public record FavoriteCafeRes(
        Long cafeId,
        String name,
        String address,
        String image
) {
}