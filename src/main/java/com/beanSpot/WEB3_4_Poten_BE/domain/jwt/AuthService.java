package com.beanSpot.WEB3_4_Poten_BE.domain.jwt;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
	private final JwtService jwtService;
	private final MemberRepository memberRepository;

	public Member validateTokenAndGetMember(String token) {
		if (token == null) {
			log.error("토큰이 null입니다.");
			throw new ServiceException(401, "로그인이 필요합니다.");
		}

		if (!jwtService.validateToken(token)) {
			log.error("유효하지 않은 토큰입니다: {}", token);
			throw new ServiceException(401, "유효하지 않은 토큰입니다.");
		}

		String oAuthId = jwtService.getOAuthIdFromToken(token);
		log.debug("토큰에서 추출한 oAuthId: {}", oAuthId);

		return memberRepository.findByOAuthId(oAuthId)
			.orElseThrow(() -> {
				log.error("사용자를 찾을 수 없습니다: {}", oAuthId);
				return new ServiceException(400, "사용자를 찾을 수 없습니다.");
			});
	}

	public ResponseEntity<?> authenticate(String token) {
		Member member = validateTokenAndGetMember(token);
		log.info("사용자 인증 성공: {}", member.getEmail());

		String newAccessToken = jwtService.generateToken(member);
		String newRefreshToken = jwtService.generateRefreshToken(member);
		log.debug("새 토큰 생성 완료");

		return ResponseEntity.ok()
			.header("Authorization", "Bearer " + newAccessToken)
			.header("RefreshToken", newRefreshToken)
			.body("인증 성공");
	}

	public ResponseEntity<?> logout(String token) {
		if (token != null && token.startsWith("Bearer ")) {
			String actualToken = token.substring(7);
			jwtService.blacklistToken(actualToken);
			log.info("토큰이 블랙리스트에 추가되었습니다.");
		} else {
			log.warn("로그아웃 요청에 유효한 토큰이 없습니다.");
		}

		return ResponseEntity.ok()
			.body("로그아웃 되었습니다. 토큰이 무효화되었습니다.");
	}
}