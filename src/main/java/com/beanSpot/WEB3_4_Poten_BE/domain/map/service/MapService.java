package com.beanSpot.WEB3_4_Poten_BE.domain.map.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

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
@Slf4j
public class MapService {

    private final CafeRepository cafeRepository;
    private final RestClient restClient;

    @Value("${kakao.key}")
    private String kakaoKey;

    @Value("${kakao.placeurl}")
    private String kakaoPlaceUrl;

    @Value("${kakao.imageurl}")
    private String kakaoImageUrl;

    /**
     * 카카오 API를 호출하여 카페 정보를 검색, Cafe 엔티티로 저장하여 리스트로 반환
     */
    public List<Cafe> searchAndSaveCafes(double x, double y, int page) {
        String apiUrl = String.format(
                "%s?query=%s&x=%f&y=%f&radius=%d&category_group_code=%s&size=%d&page=%d",
                kakaoPlaceUrl, "cafe", x, y, 2000, "CE7", 15, page
        );

        // API 요청 및 응답 처리
        ResponseEntity<Map> response = restClient.get()
                .uri(apiUrl)
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .toEntity(Map.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("카카오 API 요청 실패: " + response.getStatusCode());
        }

        // 디버깅용 카카오 API 응답 데이터
        log.debug("카카오 API 응답: {}", response.getBody());

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
     * 카페 주소로 카카오 이미지 검색 API를 호출하여 대표 이미지 URL을 가져옴
     */
    private String searchCafeImage(String address) {
        String apiUrl = String.format("%s?query=%s&size=1", kakaoImageUrl, address);

        ResponseEntity<Map> response = restClient.get()
                .uri(apiUrl)
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .toEntity(Map.class);

        // 디버깅용 응답 데이터
        log.debug("이미지 검색 응답: {}", response.getBody());

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.warn("이미지 검색 API 요청 실패: {}", response.getStatusCode());
            return null;
        }

        List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
        if (documents == null || documents.isEmpty()) {
            log.info("이미지 검색 결과 없음: {}", address);
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
            log.info("이미 존재하는 카페: {} ({})", name, address);
            return null;
        }

        // 이미지 검색 추가
        String imageUrl = searchCafeImage(address);

        // 카페 엔티티 생성
        Cafe cafe = Cafe.builder()
                .name(name)
                .address(address)
                .phone(phone)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(LocalDateTime.now())
                .imageFilename(imageUrl)
                .disabled(false)
                .build();

        return cafeRepository.save(cafe);
    }
}