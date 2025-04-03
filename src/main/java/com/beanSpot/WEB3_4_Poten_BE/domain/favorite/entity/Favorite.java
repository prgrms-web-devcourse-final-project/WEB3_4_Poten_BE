package com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

/**
 * -- 즐겨찾기 엔티티 --
 *
 * @author -- 김남우 --
 * @since -- 3월 31일 --
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cafeId")
    @JoinColumn(name = "cafe_id")
    private Cafe cafe;

    public Favorite(Member member, Cafe cafe) {
        this.id = new FavoriteId(member.getId(), cafe.getCafeId());
        this.member = member;
        this.cafe = cafe;
    }
}
