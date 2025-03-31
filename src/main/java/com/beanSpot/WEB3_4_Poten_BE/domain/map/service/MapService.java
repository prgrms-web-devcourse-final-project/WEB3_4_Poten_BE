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

    @Value("${kakao.placeurl}")
    private String kakaoPlaceUrl;

    @Value("${kakao.imageurl}")
    private String kakaoImageUrl;

    public List<Cafe> searchAndSaveCafes(double x, double y, int page) {
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = String.format(
                "%s?query=%s&x=%f&y=%f&radius=%d&category_group_code=%s&size=%d&page=%d",
                kakaoPlaceUrl, "cafe", x, y, 2000, "CE7", 15, page
        );

        // HTTP 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // API 요청 및 응답 처리
        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);
        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("카카오 API 요청 실패: " + response.getStatusCode());
        }

        // 디버깅용 카카오 API 응답 데이터
        System.out.println("카카오 API 응답: " + response.getBody());

        // documents 리스트 추출
        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
        if (documents == null || documents.isEmpty()) {
            throw new RuntimeException("조회된 카페 정보가 없습니다.");
        }

        // 응답 데이터 저장
        List<Cafe> savedCafes = new ArrayList<>();
        for (Map<String, Object> doc : documents) {
            Cafe cafe = saveCafeFromApiResponse(doc);
            if (cafe != null) {
                savedCafes.add(cafe);
            }
        }

        return savedCafes;
    }

    /**
     * 카페 이름으로 카카오 이미지 검색 API를 호출하여 대표 이미지 URL을 가져옴
     */
    private String searchCafeImage(String cafeName, String address) {
        RestTemplate restTemplate = new RestTemplate();

        String apiUrl = String.format("%s?query=%s&size=1", kakaoImageUrl, name);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoKey);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            System.out.println("이미지 검색 API 요청 실패: " + response.getStatusCode());
            return null;
        }

        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
        if (documents == null || documents.isEmpty()) {
            System.out.println("이미지 검색 결과 없음: " + cafeName);
            return null;
        }

        return (String) documents.get(0).get("image_url"); // 첫 번째 이미지 URL 반환
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

        // 이미 존재하는 카페인지 확인
        if (cafeRepository.existsByNameAndAddress(name, address)) {
            System.out.println("이미 존재하는 카페: " + name + " (" + address + ")");
            return null;
        }

        // 📌 이미지 검색 추가
        String imageUrl = searchCafeImage(name, address);

        // 카페 엔티티 생성
        Cafe cafe = Cafe.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(LocalDateTime.now())
                .image(imageUrl) // 📌 이미지 저장
                .disabled(false)
                .build();

        return cafeRepository.save(cafe);
    }
}