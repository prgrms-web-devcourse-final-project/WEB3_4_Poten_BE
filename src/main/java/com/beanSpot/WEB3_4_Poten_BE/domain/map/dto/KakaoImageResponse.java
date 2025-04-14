package com.beanSpot.WEB3_4_Poten_BE.domain.map.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Kakao 이미지 검색 API의 응답 데이터를 담는 DTO
 *
 * @author -- 김남우 --
 * @since -- 4월 12일 --
 */
@Getter
@Setter
public class KakaoImageResponse {
    private List<KakaoImageDocument> documents;
}
