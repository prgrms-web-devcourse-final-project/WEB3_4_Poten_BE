package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class JwtServiceTest {

	@Mock
	private RedisTemplate<String, Object> redisTemplate;

	@Mock
	private ValueOperations<String, Object> valueOperations;

	private JwtService jwtService;

	private Member testMember;
	private static final String SECRET_KEY = "beanspot-test-secretkey-that-is-at-least-256-bits-long-for-hs256-algorithm";

	@BeforeEach
	void setUp() {
		// ValueOperations 목 설정
		when(redisTemplate.opsForValue()).thenReturn(valueOperations);

		// JwtService 직접 생성 (InjectMocks 대신)
		jwtService = new JwtService(SECRET_KEY, redisTemplate);

		// 테스트용 회원 생성
		testMember = createTestMember();
	}

	private Member createTestMember() {
		return Member.builder()
			.id(1L)
			.name("테스트사용자")
			.email("test@example.com")
			.oAuthId("oauth-test-id-12345")
			.memberType(Member.MemberType.USER)
			.snsType(Member.SnsType.KAKAO)
			.build();
	}

	@Test
	@DisplayName("액세스 토큰 생성 테스트")
	void generateTokenTest() {
		// when
		String token = jwtService.generateToken(testMember);

		// then
		assertThat(token).isNotBlank();
		verify(redisTemplate, never()).opsForValue(); // 액세스 토큰 생성시 Redis 사용 안함
	}

	@Test
	@DisplayName("리프레시 토큰 생성 테스트")
	void generateRefreshTokenTest() {
		// when
		String refreshToken = jwtService.generateRefreshToken(testMember);

		// then
		assertThat(refreshToken).isNotBlank();
		verify(valueOperations).set(
			argThat(key -> key.startsWith("refresh:")),
			anyString());
		verify(redisTemplate).expire(
			argThat(key -> key.startsWith("refresh:")),
			eq(14L * 24L * 60L * 60L),
			eq(TimeUnit.SECONDS));
	}

	@Test
	@DisplayName("토큰 검증 성공 테스트")
	void validateTokenSuccessTest() {
		// given
		String token = jwtService.generateToken(testMember);
		when(redisTemplate.hasKey(anyString())).thenReturn(false);

		// when
		boolean isValid = jwtService.validateToken(token);

		// then
		assertThat(isValid).isTrue();
	}

	@Test
	@DisplayName("블랙리스트에 있는 토큰 검증 실패 테스트")
	void validateBlacklistedTokenTest() {
		// given
		String token = jwtService.generateToken(testMember);
		when(redisTemplate.hasKey(anyString())).thenReturn(true);

		// when
		boolean isValid = jwtService.validateToken(token);

		// then
		assertThat(isValid).isFalse();
	}

}
