package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao 장소 검색 API의 단일 장소 정보를 담는 DTO
 *
 * @author -- 김남우 --
 * @since -- 4월 12일 --
 */
@Getter
@Setter
public class KakaoPlaceDocument {
    private String id;
    private String place_name;
    private String category_name;
    private String phone;
    private String road_address_name;
    private String x;
    private String y;
}
