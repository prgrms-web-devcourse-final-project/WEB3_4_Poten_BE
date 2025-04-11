package com.beanSpot.WEB3_4_Poten_BE.global.init;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.beanSpot.WEB3_4_Poten_BE.domain.jwt.JwtService;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitializerService implements ApplicationRunner {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initAdminAccount();
		resetAdminPassword();
		initTestUser();
		generateTestUserToken();
	}

	private void initAdminAccount() {
		if (memberRepository.findByEmailAndMemberType("admin@beanspot.com", Member.MemberType.ADMIN).isPresent()) {
			log.info("관리자 계정이 이미 존재합니다.");
			return;
		}

		Member admin = Member.builder()
			.email("admin@beanspot.com")
			.name("관리자")
			.password(passwordEncoder.encode("adminPassword"))
			.oAuthId("admin")
			.memberType(Member.MemberType.ADMIN)
			.build();

		memberRepository.save(admin);
		log.info("기본 관리자 계정이 생성되었습니다.");
	}

	// 기존 관리자 비밀번호 초기화 메서드
	private void resetAdminPassword() {
		memberRepository.findByEmailAndMemberType("admin@beanspot.com", Member.MemberType.ADMIN)
			.ifPresent(admin -> {
				admin.setPassword(passwordEncoder.encode("adminPassword"));
				memberRepository.save(admin);
				log.info("관리자 비밀번호가 초기화되었습니다.");
			});
	}

	// 테스트 사용자 생성 메서드
	private void initTestUser() {
		// 카카오 테스트 사용자
		if (memberRepository.findByEmail("test-kakao@example.com").isEmpty()) {
			Member kakaoUser = Member.builder()
				.name("카카오테스트사용자")
				.email("test-kakao@example.com")
				.oAuthId("oauth-kakao-id-12345")
				.memberType(Member.MemberType.USER)
				.snsType(Member.SnsType.KAKAO)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

			memberRepository.save(kakaoUser);
			log.info("카카오 테스트 사용자가 생성되었습니다. ID: {}", kakaoUser.getId());
		} else {
			log.info("카카오 테스트 사용자가 이미 존재합니다.");
		}

		// 네이버 테스트 사용자
		if (memberRepository.findByEmail("test-naver@example.com").isEmpty()) {
			Member naverUser = Member.builder()
				.name("네이버테스트사용자")
				.email("test-naver@example.com")
				.oAuthId("oauth-naver-id-12345")
				.memberType(Member.MemberType.USER)
				.snsType(Member.SnsType.NAVER)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

			memberRepository.save(naverUser);
			log.info("네이버 테스트 사용자가 생성되었습니다. ID: {}", naverUser.getId());
		} else {
			log.info("네이버 테스트 사용자가 이미 존재합니다.");
		}

		// 구글 테스트 사용자
		if (memberRepository.findByEmail("test-google@example.com").isEmpty()) {
			Member googleUser = Member.builder()
				.name("구글테스트사용자")
				.email("test-google@example.com")
				.oAuthId("oauth-google-id-12345")
				.memberType(Member.MemberType.USER)
				.snsType(Member.SnsType.GOOGLE)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

			memberRepository.save(googleUser);
			log.info("구글 테스트 사용자가 생성되었습니다. ID: {}", googleUser.getId());
		} else {
			log.info("구글 테스트 사용자가 이미 존재합니다.");
		}
	}

	// 모든 테스트 사용자의 토큰 생성 메서드
	private void generateTestUserToken() {
		// 카카오 사용자 토큰
		Member kakaoUser = memberRepository.findByEmail("test-kakao@example.com").orElse(null);
		if (kakaoUser != null) {
			String kakaoAccessToken = jwtService.generateToken(kakaoUser);
			log.info("카카오 테스트 사용자 액세스 토큰: {}", kakaoAccessToken);
		}

		// 네이버 사용자 토큰
		Member naverUser = memberRepository.findByEmail("test-naver@example.com").orElse(null);
		if (naverUser != null) {
			String naverAccessToken = jwtService.generateToken(naverUser);
			log.info("네이버 테스트 사용자 액세스 토큰: {}", naverAccessToken);
		}

		// 구글 사용자 토큰
		Member googleUser = memberRepository.findByEmail("test-google@example.com").orElse(null);
		if (googleUser != null) {
			String googleAccessToken = jwtService.generateToken(googleUser);
			log.info("구글 테스트 사용자 액세스 토큰: {}", googleAccessToken);
		}
	}
}