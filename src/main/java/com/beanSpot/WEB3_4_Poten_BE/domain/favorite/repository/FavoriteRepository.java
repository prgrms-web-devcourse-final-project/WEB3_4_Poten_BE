package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.FavoriteId;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * -- 즐겨찾기 레포지토리 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByMemberAndCafe(Member member, Cafe cafe);

    List<Favorite> findByMember(Member member);

    Optional<Favorite> findById(FavoriteId favoriteId);
}
