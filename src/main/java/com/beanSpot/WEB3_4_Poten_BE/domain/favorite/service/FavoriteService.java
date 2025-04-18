package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto.FavoriteCafeRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.FavoriteId;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository.FavoriteRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * -- 즐겨찾기 서비스 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
@RequiredArgsConstructor
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final MemberRepository memberRepository;
    private final CafeRepository cafeRepository;

    /**
     * 즐겨찾기 추가
     */
    public void addFavorite(Long memberId, Long cafeId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 카페입니다."));

        // 이미 즐겨찾기한 경우 예외 처리
        if (favoriteRepository.findById(new FavoriteId(memberId, cafeId)).isPresent()) {
            throw new IllegalStateException("이미 즐겨찾기에 추가된 카페입니다.");
        }

        Favorite favorite = new Favorite(member, cafe);

        favoriteRepository.save(favorite);
    }

    /**
     * 즐겨찾기 삭제
     */
    public void removeFavorite(Long memberId, Long cafeId) {
        FavoriteId favoriteId = new FavoriteId(memberId, cafeId);

        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기에 없는 카페입니다."));

        favoriteRepository.delete(favorite);
    }

    /**
     * 즐겨찾기 조회
     */
    public List<FavoriteCafeRes> getFavoriteCafes(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return favoriteRepository.findByMember(member).stream()
                .map(favorite -> FavoriteCafeRes.builder()
                        .cafeId(favorite.getCafe().getCafeId())
                        .name(favorite.getCafe().getName())
                        .address(favorite.getCafe().getAddress())
                        .image(favorite.getCafe().getImage())
                        .build()
                )
                .collect(Collectors.toList());
    }
}
