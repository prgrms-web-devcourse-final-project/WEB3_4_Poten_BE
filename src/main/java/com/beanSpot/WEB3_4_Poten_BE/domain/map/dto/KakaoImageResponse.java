package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class KakaoImageResponse {
    private List<KakaoImageDocument> documents;
}
