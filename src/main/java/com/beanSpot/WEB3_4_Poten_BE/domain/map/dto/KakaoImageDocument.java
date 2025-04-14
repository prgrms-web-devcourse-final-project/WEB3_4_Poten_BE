package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Kakao 이미지 검색 API 응답의 단일 이미지 문서를 담는 DTO
 *
 * @author -- 김남우 --
 * @since -- 4월 12일 --
 */
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
