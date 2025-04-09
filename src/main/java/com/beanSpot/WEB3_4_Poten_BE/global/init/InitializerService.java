package com.beanSpot.WEB3_4_Poten_BE.global.initializer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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

	@Override
	public void run(ApplicationArguments args) throws Exception {
		initAdminAccount();
		resetAdminPassword();
	}

	private void initAdminAccount() {
		// 기존 코드와 동일
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
}