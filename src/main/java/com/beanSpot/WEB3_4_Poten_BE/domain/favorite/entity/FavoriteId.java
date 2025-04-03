package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * -- 즐겨찾기 복합키 --
 * @author -- 김남우 --
 * @since -- 4월 3일 --
 */
@Embeddable
@Getter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteId implements Serializable {
    private Long memberId;
    private Long cafeId;
}