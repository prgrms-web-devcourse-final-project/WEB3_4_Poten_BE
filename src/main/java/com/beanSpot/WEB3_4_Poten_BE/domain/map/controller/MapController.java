package com.beanSpot.WEB3_4_Poten_BE.domain.map.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.map.service.MapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * -- 지도 컨트롤러 --
 *
 * @author -- 김남우 --
 * @since -- 3월 25일 --
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    @GetMapping
    public ResponseEntity<List<Cafe>> searchAndSaveCafes(
            @RequestParam double x,
            @RequestParam double y,
            @RequestParam int page) {
        List<Cafe> Cafes = mapService.searchAndSaveCafes(x, y, page);
        return ResponseEntity.ok(Cafes);
    }
}
