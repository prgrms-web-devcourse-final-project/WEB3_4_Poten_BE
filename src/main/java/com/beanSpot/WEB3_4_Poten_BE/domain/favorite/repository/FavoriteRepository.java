package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * -- 즐겨찾기 레포지토리 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
}
