package com.beanSpot.WEB3_4_Poten_BE.domain.oauth;

import java.util.Map;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import lombok.Getter;

@Getter
public class SecurityUser extends User implements OAuth2User {
	public final long id;
	public final String nickname;
	public final String email;
	public final Member member;

	public SecurityUser(Member member) {
		super(
			member.getUsername(),  // oAuthId를 username으로 사용
			"",                   // 비밀번호는 빈 문자열
			member.getAuthorities() // Member에서 정의한 권한 사용
		);
		this.id = member.getId();
		this.nickname = member.getName();
		this.email = member.getEmail(); // email 필드명 주의
		this.member = member;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return Map.of(
			"id", id,
			"nickname", nickname,
			"email", email,
			"memberType", member.getMemberType()
		);
	}

	@Override
	public String getName() {
		return getUsername();
	}
}