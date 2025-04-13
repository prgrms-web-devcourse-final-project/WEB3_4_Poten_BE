package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

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
