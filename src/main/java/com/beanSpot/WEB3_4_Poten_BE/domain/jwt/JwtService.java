package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	// 기존 필드
	private final Key key;
	private static final long TOKEN_VALIDITY = 14 * 24 * 60 * 60 * 1000L;  // 1시간
	private static final long REFRESH_TOKEN_VALIDITY = 14 * 24 * 60 * 60 * 1000L;  // 7일

	// Redis 관련 필드 추가
	private final RedisTemplate<String, Object> redisTemplate;
	private static final String REFRESH_TOKEN_PREFIX = "refresh:";
	private static final String BLACK_LIST_PREFIX = "blacklist:";

	// 생성자 수정
	public JwtService(@Value("${custom.jwt.secretKey}") String secretKey,
		RedisTemplate<String, Object> redisTemplate) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
		this.redisTemplate = redisTemplate;
	}

	// 기존 토큰 생성 메소드 유지
	public String generateToken(Member member) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + TOKEN_VALIDITY);

		String token = Jwts.builder()
			.setSubject(member.getOAuthId())
			.claim("id", member.getId())
			.claim("email", member.getEmail())
			.claim("name", member.getName())
			.claim("role", member.getMemberType().toString())
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(key)
			.compact();

		return URLEncoder.encode(token, StandardCharsets.UTF_8);
	}

	// 토큰에서 클레임 추출하는 메소드
	private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		try {
			String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(decodedToken)
				.getBody();
			return claimsResolver.apply(claims);
		} catch (Exception e) {
			log.error("토큰에서 정보 추출 실패: {}", e.getMessage());
			throw new ServiceException(401, "토큰 정보 추출 실패: " + e.getMessage());
		}
	}

	// 모든 클레임 가져오기
	public Map<String, Object> getAllClaimsFromToken(String token) {
		return extractClaim(token, claims -> claims);
	}

	// 사용자 ID 가져오기
	public String getOAuthIdFromToken(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	// Redis 사용하도록 리프레시 토큰 생성 메소드 수정
	public String generateRefreshToken(Member member) {
		String token = Jwts.builder()
			.setSubject(member.getOAuthId())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
			.signWith(key)
			.compact();

		String encodedToken = URLEncoder.encode(token, StandardCharsets.UTF_8);

		// Redis에 리프레시 토큰 저장
		String redisKey = REFRESH_TOKEN_PREFIX + member.getOAuthId();
		redisTemplate.opsForValue().set(redisKey, encodedToken);
		redisTemplate.expire(redisKey, REFRESH_TOKEN_VALIDITY / 1000, TimeUnit.SECONDS);

		return encodedToken;
	}

	// Redis에서 리프레시 토큰 검증
	public boolean validateRefreshToken(String oAuthId, String refreshToken) {
		String storedToken = (String) redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + oAuthId);
		return storedToken != null && storedToken.equals(refreshToken) && validateToken(refreshToken);
	}

	// 토큰 블랙리스트에 추가 (로그아웃 시 사용)
	public void blacklistToken(String token) {
		try {
			String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(decodedToken)
				.getBody();

			long ttl = claims.getExpiration().getTime() - System.currentTimeMillis();
			if (ttl > 0) {
				redisTemplate.opsForValue().set(BLACK_LIST_PREFIX + token, "blacklisted", ttl, TimeUnit.MILLISECONDS);
			}
		} catch (Exception e) {
			log.error("토큰 블랙리스트 등록 중 오류 발생: ", e);
		}
	}

	// Redis 블랙리스트 체크하도록 토큰 검증 메소드 수정
	public boolean validateToken(String token) {
		try {
			// URL 디코딩 후 검증
			log.debug("받은 토큰: {}", token);
			String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
			log.debug("디코딩된 토큰: {}", decodedToken);

			// 블랙리스트 확인
			Boolean isBlacklisted = redisTemplate.hasKey(BLACK_LIST_PREFIX + token);
			if (Boolean.TRUE.equals(isBlacklisted)) {
				log.debug("블랙리스트에 있는 토큰");
				return false;
			}

			Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(decodedToken);
			log.debug("토큰 검증 성공");
			return true;
		} catch (Exception e) {
			log.error("토큰 검증 실패: {}", e.getMessage());
			return false;
		}
	}
}