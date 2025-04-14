package com.beanSpot.WEB3_4_Poten_BE.domain.map;

import com.beanSpot.WEB3_4_Poten_BE.domain.map.dto.KakaoPlaceDocument;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.service.MapService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class MapServiceTest {

    @Autowired
    private MapService mapService;

    @Test
    public void testFetchCafesFromKakao() {
        List<KakaoPlaceDocument> cafes = mapService.fetchCafesFromKakao(128.9783, 35.1627, 1);
        
        assertNotNull(cafes);
        assertFalse(cafes.isEmpty(), "카카오 API로부터 카페 목록을 받아와야 함");
        System.out.println("가져온 카페 수: " + cafes.size());
        cafes.forEach(c -> System.out.println("카페 이름: " + c.getPlace_name()));
    }
}
