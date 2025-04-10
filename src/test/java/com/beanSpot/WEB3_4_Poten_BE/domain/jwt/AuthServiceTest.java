package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AuthServiceTest {

	@Mock
	private JwtService jwtService;

	@Mock
	private MemberRepository memberRepository;

	@InjectMocks
	private AuthService authService;

	private Member testMember;
	private String testToken;

	@BeforeEach
	void setUp() {
		testMember = createTestMember();
		testToken = "valid.jwt.token";

		// 기본 스터빙 설정
		when(jwtService.validateToken(testToken)).thenReturn(true);
		when(jwtService.getOAuthIdFromToken(testToken)).thenReturn(testMember.getOAuthId());
		when(memberRepository.findByoAuthId(testMember.getOAuthId())).thenReturn(Optional.of(testMember));
		when(jwtService.generateToken(testMember)).thenReturn("new.access.token");
		when(jwtService.generateRefreshToken(testMember)).thenReturn("new.refresh.token");
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
	@DisplayName("토큰으로 사용자 검증 성공 테스트")
	void validateTokenAndGetMemberSuccessTest() {

		// when
		Member result = authService.validateTokenAndGetMember(testToken);

		// then
		assertThat(result).isEqualTo(testMember);
		verify(jwtService).validateToken(testToken);
		verify(jwtService).getOAuthIdFromToken(testToken);
		verify(memberRepository).findByoAuthId(testMember.getOAuthId());
	}

	@Test
	@DisplayName("토큰이 없는 경우 예외 발생 테스트 - 경계 조건 테스트")
	void validateTokenAndGetMemberEmptyTokenTest() {
		// when, then
		assertThrows(ServiceException.class,
			() -> authService.validateTokenAndGetMember(null));

		verify(jwtService, never()).validateToken(anyString());
		verify(jwtService, never()).getOAuthIdFromToken(anyString());
		verify(memberRepository, never()).findByoAuthId(anyString());
	}

	@Test
	@DisplayName("빈 문자열 토큰 테스트 - 경계 조건 테스트")
	void validateTokenAndGetMemberEmptyStringTokenTest() {
		// when, then
		ServiceException exception = assertThrows(ServiceException.class,
			() -> authService.validateTokenAndGetMember(""));

		assertThat(exception.getResultCode()).isEqualTo(401);
		assertThat(exception.getMessage()).contains("유효하지 않은 토큰입니다");
	}

	@Test
	@DisplayName("유효하지 않은 토큰 테스트")
	void validateTokenAndGetMemberInvalidTokenTest() {
		// given
		when(jwtService.validateToken("invalid.token")).thenReturn(false);

		// when, then
		ServiceException exception = assertThrows(ServiceException.class,
			() -> authService.validateTokenAndGetMember("invalid.token"));

		assertThat(exception.getResultCode()).isEqualTo(401);
		assertThat(exception.getMessage()).contains("유효하지 않은 토큰입니다");
	}

	@Test
	@DisplayName("사용자를 찾을 수 없는 경우 테스트")
	void validateTokenAndGetMemberUserNotFoundTest() {
		// given
		String unknownUserToken = "unknown.user.token";
		when(jwtService.validateToken(unknownUserToken)).thenReturn(true);
		when(jwtService.getOAuthIdFromToken(unknownUserToken)).thenReturn("unknown-oauth-id");
		when(memberRepository.findByoAuthId("unknown-oauth-id")).thenReturn(Optional.empty());

		// when, then
		ServiceException exception = assertThrows(ServiceException.class,
			() -> authService.validateTokenAndGetMember(unknownUserToken));

		assertThat(exception.getResultCode()).isEqualTo(400);
		assertThat(exception.getMessage()).contains("사용자를 찾을 수 없습니다");
	}

	@Test
	@DisplayName("인증 성공 및 토큰 재발급 테스트")
	void authenticateSuccessTest() {
		// given - setUp에서 설정됨

		// when
		ResponseEntity<?> response = authService.authenticate(testToken);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getHeaders().getFirst("Authorization")).isEqualTo("Bearer new.access.token");
		assertThat(response.getHeaders().getFirst("RefreshToken")).isEqualTo("new.refresh.token");

		// 토큰 재발급 검증
		verify(jwtService).generateToken(testMember);
		verify(jwtService).generateRefreshToken(testMember);
	}

	@Test
	@DisplayName("토큰 재발급 시 올바른 사용자 정보 사용 검증")
	void tokenRegenerationWithCorrectUserInfoTest() {
		// given - setUp에서 설정됨

		// when
		authService.authenticate(testToken);

		// then - 정확한 회원 정보로 토큰이 생성되는지 검증
		verify(jwtService).generateToken(eq(testMember));
		verify(jwtService).generateRefreshToken(eq(testMember));
	}

	@Test
	@DisplayName("로그아웃 테스트 - 토큰 블랙리스트 등록 검증")
	void logoutTest() {
		// given
		String tokenWithBearer = "Bearer " + testToken;

		// when
		ResponseEntity<?> response = authService.logout(tokenWithBearer);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody().toString()).contains("로그아웃 되었습니다");

		// 토큰 블랙리스트 등록 검증
		verify(jwtService).blacklistToken(testToken);
	}

	@Test
	@DisplayName("로그아웃 시 Bearer 접두사 없는 토큰 처리 테스트")
	void logoutWithoutBearerPrefixTest() {
		// given
		String tokenWithoutBearer = testToken;

		// when
		ResponseEntity<?> response = authService.logout(tokenWithoutBearer);

		// then
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		// 블랙리스트 호출 안함 확인
		verify(jwtService, never()).blacklistToken(anyString());
	}

	@Test
	@DisplayName("로그아웃된 토큰(블랙리스트)으로 인증 시도 테스트")
	void validateTokenAndGetMemberWithBlacklistedTokenTest() {
		// given
		String blacklistedToken = "blacklisted.token";

		// jwtService의 validateToken 메서드가 false를 반환하도록 설정
		// (JwtService 내부에서 블랙리스트 확인 후 false 반환)
		when(jwtService.validateToken(blacklistedToken)).thenReturn(false);

		// when, then
		ServiceException exception = assertThrows(ServiceException.class,
			() -> authService.validateTokenAndGetMember(blacklistedToken));

		// 예외 확인
		assertThat(exception.getResultCode()).isEqualTo(401);
		assertThat(exception.getMessage()).contains("유효하지 않은 토큰입니다");

		// jwtService의 validateToken 메서드가 호출되었는지 확인
		verify(jwtService).validateToken(blacklistedToken);

		// 블랙리스트된 토큰이므로 getOAuthIdFromToken은 호출되지 않아야 함
		verify(jwtService, never()).getOAuthIdFromToken(anyString());
	}
}
