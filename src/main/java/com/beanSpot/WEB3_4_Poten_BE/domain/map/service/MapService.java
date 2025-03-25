package com.beanSpot.WEB3_4_Poten_BE.domain.map.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * -- 지도 서비스 --
 *
 * @author -- 김남우 --
 * @since -- 3월 25일 --
 */
@RequiredArgsConstructor
@Service
public class MapService {

    @Value("${kakao.key}")
    private String kakaoKey;

    @Value("${kakao.url}")
    private String kakaoUrl;

    public String getGeocode(String address) {
        RestTemplate restTemplate = new RestTemplate();
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(kakaoUrl)
                .queryParam("query", address);

        // 헤더에 카카오 API 키를 추가
        ResponseEntity<Map> response = restTemplate.exchange(
                uriBuilder.toUriString(),
                org.springframework.http.HttpMethod.GET,
                new org.springframework.http.HttpEntity<>(getHeaders()),
                Map.class
        );

        // 응답을 처리하고 반환
        Map<String, Object> responseBody = response.getBody();
        return responseBody != null ? responseBody.toString() : "No data";
    }

    private org.springframework.http.HttpHeaders getHeaders() {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);
        return headers;
    }
}
