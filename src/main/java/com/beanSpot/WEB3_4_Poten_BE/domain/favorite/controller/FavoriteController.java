package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto.FavoriteCafeRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * -- 즐겨찾기 컨트롤러 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
@Tag(name = "Favorite", description = "Favorite Controller")
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(
            summary = "카페 즐겨찾기 추가",
            description = "카페 즐겨찾기 추가")
    @PostMapping("/{memberId}/{cafeId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long memberId, @PathVariable Long cafeId) {
        favoriteService.addFavorite(memberId, cafeId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "카페 즐겨찾기 삭제",
            description = "카페 즐겨찾기 삭제")
    @DeleteMapping("/{memberId}/{cafeId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long memberId, @PathVariable Long cafeId) {
        favoriteService.removeFavorite(memberId, cafeId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "카페 즐겨찾기 조회",
            description = "즐겨찾기한 카페 조회")
    @GetMapping("/{memberId}")
    public ResponseEntity<List<FavoriteCafeRes>> getFavorites(@PathVariable Long memberId) {
        List<FavoriteCafeRes> favoriteCafes = favoriteService.getFavoriteCafes(memberId);
        return ResponseEntity.ok(favoriteCafes);
    }

}
