package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto.FavoriteCafeRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.service.FavoriteService;
import com.beanSpot.WEB3_4_Poten_BE.domain.oauth.SecurityUser;

import lombok.RequiredArgsConstructor;

/**
 * -- 즐겨찾기 컨트롤러 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;

   /* @PostMapping("/{memberId}/{cafeId}")
    public ResponseEntity<String> addFavorite(@PathVariable Long memberId, @PathVariable Long cafeId) {
        favoriteService.addFavorite(memberId, cafeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{memberId}/{cafeId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long memberId, @PathVariable Long cafeId) {
        favoriteService.removeFavorite(memberId, cafeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<List<FavoriteCafeRes>> getFavorites(@PathVariable Long memberId) {
        List<FavoriteCafeRes> favoriteCafes = favoriteService.getFavoriteCafes(memberId);
        return ResponseEntity.ok(favoriteCafes);
    }*/

    //TODO: 인증 구현 후 userId는 RequestBody에서 제거하고 SecurityContext에서 가져오기
    @PostMapping("/{cafeId}")
    public ResponseEntity<String> addFavorite(
        @PathVariable Long cafeId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        favoriteService.addFavorite(securityUser.getMember().getId(), cafeId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{cafeId}")
    public ResponseEntity<String> removeFavorite(
        @PathVariable Long cafeId,
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        favoriteService.removeFavorite(securityUser.getMember().getId(), cafeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<FavoriteCafeRes>> getFavorites(
        @AuthenticationPrincipal SecurityUser securityUser
    ) {
        List<FavoriteCafeRes> favoriteCafes = favoriteService.getFavoriteCafes(securityUser.getMember().getId());
        return ResponseEntity.ok(favoriteCafes);
    }

}
