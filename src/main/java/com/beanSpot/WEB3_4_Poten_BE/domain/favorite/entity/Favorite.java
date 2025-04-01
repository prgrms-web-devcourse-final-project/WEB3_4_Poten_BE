package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * -- 즐겨찾기 엔티티 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;
}
