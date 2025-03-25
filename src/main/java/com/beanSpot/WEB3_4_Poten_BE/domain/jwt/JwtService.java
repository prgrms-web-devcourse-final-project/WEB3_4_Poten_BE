package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ll.nbe342team8.domain.member.member.entity.Member;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	// 서명 키를 한 번만 생성하여 재사용
	private final Key key;
	private static final long TOKEN_VALIDITY = 60 * 60 * 1000L;  // 1시간
	private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000L;  // 7일
	private final Set<String> tokenBlacklist = ConcurrentHashMap.newKeySet();

	// 생성자에서 키를 초기화하여 일관성 보장
	public JwtService(@Value("${custom.jwt.secretKey}") String secretKey) {
		byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
		this.key = Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Member member) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + TOKEN_VALIDITY);

		String token = Jwts.builder()
			.setSubject(member.getOAuthId())
			.claim("id", member.getId())
			.claim("email", member.getEmail())
			.claim("name", member.getName())
			.setIssuedAt(now)
			.setExpiration(validity)
			.signWith(key)
			.compact();

		// URL 안전한 형태로 인코딩
		return URLEncoder.encode(token, StandardCharsets.UTF_8);
	}

	public String generateRefreshToken(Member member) {
		String token = Jwts.builder()
			.setSubject(member.getOAuthId())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
			.signWith(key)
			.compact();

		return URLEncoder.encode(token, StandardCharsets.UTF_8);
	}

	public boolean validateToken(String token) {
		try {
			// URL 디코딩 후 검증
			log.debug("받은 토큰: {}", token);
			String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
			log.debug("디코딩된 토큰: {}", decodedToken);

			if (tokenBlacklist.contains(decodedToken)) {
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
			log.error("토큰 검증 실패: ", e);
			log.error("상세 에러: ", e);
			return false;
		}
	}

	public String getKakaoIdFromToken(String token) {
		try {
			String decodedToken = URLDecoder.decode(token, StandardCharsets.UTF_8);
			return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(decodedToken)
				.getBody()
				.getSubject();
		} catch (Exception e) {
			log.error("토큰에서 카카오 ID 추출 실패: ", e);
			throw e;
		}
	}
}