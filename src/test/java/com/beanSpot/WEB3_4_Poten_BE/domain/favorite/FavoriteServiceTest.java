package com.beanSpot.WEB3_4_Poten_BE.domain.favorite;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.dto.FavoriteCafeRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.Favorite;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.entity.FavoriteId;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.repository.FavoriteRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.favorite.service.FavoriteService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member.MemberType;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member.SnsType;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private CafeRepository cafeRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private Member member;
    private Cafe cafe;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = Member.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .username("john_doe")
                .password("encoded_password")
                .profileImg("profile.jpg")
                .oAuthId("oauth-1234")
                .phoneNumber("010-1111-2222")
                .memberType(MemberType.USER)
                .snsType(SnsType.KAKAO)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        cafe = Cafe.builder()
                .cafeId(1L)
                .owner(member)
                .name("Cafe Mocha")
                .address("Seoul")
                .latitude(37.123)
                .longitude(127.123)
                .phone("010-1234-5678")
                .description("A nice cafe")
                .createdAt(LocalDateTime.now())
                .imageFilename("cafe.jpg")
                .capacity(20)
                .build();
    }

    @Test
    void 즐겨찾기_추가_성공() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(favoriteRepository.findById(any(FavoriteId.class))).thenReturn(Optional.empty());

        favoriteService.addFavorite(1L, 1L);

        verify(favoriteRepository).save(any(Favorite.class));
    }

    @Test
    void 즐겨찾기_추가_중복_예외() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(favoriteRepository.findById(any(FavoriteId.class)))
                .thenReturn(Optional.of(new Favorite(member, cafe)));

        assertThrows(IllegalStateException.class, () -> {
            favoriteService.addFavorite(1L, 1L);
        });
    }

    @Test
    void 즐겨찾기_삭제_성공() {
        FavoriteId favoriteId = new FavoriteId(1L, 1L);
        Favorite favorite = new Favorite(member, cafe);

        when(favoriteRepository.findById(favoriteId)).thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(1L, 1L);

        verify(favoriteRepository).delete(favorite);
    }

    @Test
    void 즐겨찾기_조회_성공() {
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(favoriteRepository.findByMember(member)).thenReturn(List.of(new Favorite(member, cafe)));

        List<FavoriteCafeRes> result = favoriteService.getFavoriteCafes(1L);

        System.out.println("즐겨찾기 테스트 조회 결과:");
        for (FavoriteCafeRes res : result) {
            System.out.println(" - " + res);
        }

        assertThat(result).hasSize(1);
        assertThat(result.get(0).cafeId()).isEqualTo(1L);
    }
}
