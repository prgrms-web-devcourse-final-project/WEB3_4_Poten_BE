package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * -- 즐겨찾기 레포지토리 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    // 특정 사용자가 특정 카페를 즐겨찾기 했는지 확인
    Optional<Favorite> findByMemberAndCafe(Member member, Cafe cafe);
}
