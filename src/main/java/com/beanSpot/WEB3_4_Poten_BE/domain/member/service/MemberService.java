package com.beanSpot.WEB3_4_Poten_BE.domain.member.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.dto.req.UpdateMemberMyPageDto;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.repository.MemberRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepository;

	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	@Transactional
	public Member modifyOrJoin(String OAuthId, String email, String name, String profileImg, Member.SnsType snsType) {
		return memberRepository.findByOAuthId(OAuthId) // 기존 회원인지 확인 (OAuthId 기준으로 검색)
			.map(member -> {
				// 기존 회원 정보 업데이트
				member.setName(name);
				member.setEmail(email);
				member.setProfileImg(profileImg);
				member.setSnsType(snsType);
				return memberRepository.save(member);
			})
			.orElseGet(() -> {
				// 새 회원 생성 시 기본값으로 USER 타입 설정
				Member member = Member.builder()
					.OAuthId(OAuthId)
					.email(email)
					.name(name)
					.profileImg(profileImg)
					.snsType(snsType)
					.memberType(Member.MemberType.USER)
					.build();
				return memberRepository.save(member);
			});
	}

	@Transactional
	public Member updateMemberInfo(String OAuthId, UpdateMemberMyPageDto dto, String currentEmail) {
		return memberRepository.findByOAuthId(OAuthId)
			.map(member -> {
				// 이메일 중복 확인 (현재 자신의 이메일이 아닌 다른 이메일로 변경하려는 경우)
				if (dto.email() != null && !dto.email().equals(currentEmail)) {
					memberRepository.findByEmail(dto.email())
						.ifPresent(m -> {
							// 다른 사용자가 이미 사용 중인 이메일인 경우
							if (!m.getOAuthId().equals(OAuthId)) {
								throw new ServiceException("이미 사용 중인 이메일입니다.");
							}
						});
					member.setEmail(dto.email());
				}

				// 기존 회원 정보 업데이트
				if (dto.name() != null) {
					member.setName(dto.name());
				}
				if (dto.phoneNumber() != null) {
					member.setPhoneNumber(dto.phoneNumber());
				}

				return memberRepository.save(member);
			})
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));
	}

	public Member getMemberById(Long id) {
		return memberRepository.findById(id)
			.orElseThrow(() -> new ServiceException("사용자를 찾을 수 없습니다."));
	}

	public Member create(Member member) {
		return memberRepository.save(member);
	}

	public long count() {
		return memberRepository.count();
	}

	public Optional<Member> findByOAuthId(String oAuthId) {
		return memberRepository.findByOAuthId(oAuthId);
	}

	@Override
	public UserDetails loadUserByUsername(String oAuthId) throws UsernameNotFoundException {
		Member member = findByOAuthId(oAuthId)
			.orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		// 사용자 역할에 따른 권한 설정
		List<SimpleGrantedAuthority> authorities = getAuthoritiesByMemberType(member.getMemberType());

		return new User(
			member.getOAuthId(),
			"", // OAuth 로그인이므로 비밀번호는 비워둠
			authorities
		);
	}

	// 사용자 역할에 따른 권한 설정 메서드
	private List<SimpleGrantedAuthority> getAuthoritiesByMemberType(Member.MemberType memberType) {
		switch (memberType) {
			case ADMIN:
				return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
			case OWNER:
				return List.of(
					new SimpleGrantedAuthority("ROLE_USER"),
					new SimpleGrantedAuthority("ROLE_OWNER")
				);
			default: // USER
				return List.of(new SimpleGrantedAuthority("ROLE_USER"));
		}
	}
}