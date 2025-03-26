package com.beanSpot.WEB3_4_Poten_BE.domain.map.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    private final CafeRepository cafeRepository;

    @Value("${kakao.key}")
    private String kakaoKey;

    @Value("${kakao.url}")
    private String kakaoUrl;

    public List<Cafe> searchAndSaveCafes(String address) {
        RestTemplate restTemplate = new RestTemplate();

        // 요청 URL 생성 (키워드 기반 카페 검색)
        String apiUrl = UriComponentsBuilder.fromHttpUrl(kakaoUrl)
                .queryParam("query", address)
                .toUriString();

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 요청 및 응답 처리
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            System.out.println("카카오 API 요청 실패: " + response.getStatusCode());
            throw new RuntimeException("카카오 API 요청 실패");
        }

        // 카카오 API 응답 데이터 출력 (디버깅용)
        System.out.println("카카오 API 응답: " + response.getBody());

        // 응답 데이터에서 'documents' 리스트 추출
        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
        if (documents == null || documents.isEmpty()) {
            System.out.println("조회된 카페 정보가 없습니다.");
            return Collections.emptyList(); // 빈 리스트 반환
        }

        List<Cafe> savedCafes = new ArrayList<>();

        // 카페 정보 저장
        for (Map<String, Object> doc : documents) {
            Cafe cafe = saveCafeFromApiResponse(doc);
            if (cafe != null) {
                savedCafes.add(cafe);
            }
        }

        return savedCafes; // 저장된 카페 목록 반환
    }

    /**
     * API 응답 데이터를 Cafe 엔티티로 변환 후 저장
     */
    private Cafe saveCafeFromApiResponse(Map<String, Object> doc) {
        String name = (String) doc.get("place_name");
        String address = (String) doc.get("road_address_name");
        String phone = (String) doc.get("phone");
        Double latitude = Double.valueOf(doc.get("y").toString());
        Double longitude = Double.valueOf(doc.get("x").toString());

        // 이미 존재하는 카페인지 확인 (주소로 중복 체크)
        if (cafeRepository.existsByAddress(address)) {
            System.out.println("이미 존재하는 카페: " + address);
            return null;
        }

        // 카페 엔티티 생성
        Cafe cafe = Cafe.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(LocalDateTime.now())
                .disabled(false)
                .build();

        // 카페 저장 후 반환
        return cafeRepository.save(cafe);
    }
}
