package com.beanSpot.WEB3_4_Poten_BE.domain.map.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.dto.KakaoImageDocument;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.dto.KakaoImageResponse;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.dto.KakaoPlaceDocument;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.dto.KakaoPlaceResponse;
import com.beanSpot.WEB3_4_Poten_BE.global.common.TransactionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private final TransactionTemplate transactionTemplate;

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
        return transactionTemplate.execute(status -> {
            try {
                List<KakaoPlaceDocument> documents = fetchCafesFromKakao(x, y, page);

                List<Cafe> savedCafes = new ArrayList<>();
                for (KakaoPlaceDocument doc : documents) {
                    try {
                        Cafe cafe = TransactionHelper.execute(() -> saveCafeFromPlaceDocument(doc));
                        if (cafe != null) {
                            savedCafes.add(cafe);
                        }
                    } catch (Exception e) {
                        log.warn("카페 저장 실패 - 무시하고 계속 진행: {}", doc.getPlace_name(), e);
                    }
                }

                if (savedCafes.isEmpty()) {
                    throw new RuntimeException("카페 저장에 실패했습니다. 롤백합니다.");
                }

                return savedCafes;
            } catch (Exception e) {
                status.setRollbackOnly();
                throw e;
            }
        });
    }

    /**
     * 카카오 장소 검색 API를 호출하여 카페 정보를 가져옴
     */
    private List<KakaoPlaceDocument> fetchCafesFromKakao(double x, double y, int page) {
        String apiUrl = String.format(
                "%s?query=%s&x=%f&y=%f&radius=%d&category_group_code=%s&size=%d&page=%d",
                kakaoPlaceUrl, "cafe", x, y, 2000, "CE7", 15, page
        );

        ResponseEntity<KakaoPlaceResponse> response = restClient.get()
                .uri(apiUrl)
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .toEntity(KakaoPlaceResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new RuntimeException("카카오 API 요청 실패: " + response.getStatusCode());
        }

        log.debug("카카오 API 응답: {}", response.getBody());

        List<KakaoPlaceDocument> documents = response.getBody().getDocuments();
        if (documents == null || documents.isEmpty()) {
            throw new RuntimeException("조회된 카페 정보가 없습니다.");
        }

        return documents;
    }

    /**
     * 카페 주소로 카카오 이미지 검색 API를 호출하여 대표 이미지 URL을 가져옴
     */
    private String searchCafeImage(String address) {
        String apiUrl = String.format("%s?query=%s&size=1", kakaoImageUrl, address);

        ResponseEntity<KakaoImageResponse> response = restClient.get()
                .uri(apiUrl)
                .header("Authorization", "KakaoAK " + kakaoKey)
                .retrieve()
                .toEntity(KakaoImageResponse.class);

        if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            log.warn("이미지 검색 API 요청 실패: {}", response.getStatusCode());
            return null;
        }

        List<KakaoImageDocument> documents = response.getBody().getDocuments();
        if (documents == null || documents.isEmpty()) {
            log.info("이미지 검색 결과 없음: {}", address);
            return null;
        }

        return documents.get(0).getImage_url();
    }

    /**
     * API 응답 데이터를 Cafe 엔티티로 변환 후 저장
     */
    private Cafe saveCafeFromPlaceDocument(KakaoPlaceDocument doc) {
        String name = doc.getPlace_name();
        String address = doc.getRoad_address_name();
        String phone = doc.getPhone();
        Double latitude = Double.valueOf(doc.getY());
        Double longitude = Double.valueOf(doc.getX());

        if (cafeRepository.existsByNameAndAddress(name, address)) {
            log.info("이미 존재하는 카페: {} ({})", name, address);
            return null;
        }

        String imageUrl = searchCafeImage(address);

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