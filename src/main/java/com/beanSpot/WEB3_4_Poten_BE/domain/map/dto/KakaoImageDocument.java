package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KakaoImageDocument {
    private String image_url;
    private String thumbnail_url;
    private String doc_url;
    private String display_sitename;
    private String collection;
    private int width;
    private int height;
}
