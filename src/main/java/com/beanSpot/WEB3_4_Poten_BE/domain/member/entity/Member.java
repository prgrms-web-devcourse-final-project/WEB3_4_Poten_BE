package com.beanSpot.WEB3_4_Poten_BE.domain.member.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	private String username;

	private String password;

	private String profileImg;

	private String OAuthId;

	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private MemberType memberType;

	@Enumerated(EnumType.STRING)
	private SnsType snsType;

	public enum MemberType {
		USER, ADMIN, OWNER
	}

	public enum SnsType {
		KAKAO, NAVER, GOOGLE
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();

		// 기본적으로 모든 일반 사용자는 USER 권한을 가집니다
		if (this.memberType == MemberType.USER) {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		}

		// OWNER는 USER 권한도 가지고 OWNER 권한도 가집니다
		if (this.memberType == MemberType.OWNER) {
			authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
			authorities.add(new SimpleGrantedAuthority("ROLE_OWNER"));
		}

		// ADMIN은 ADMIN 권한을 가집니다
		if (this.memberType == MemberType.ADMIN) {
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		}

		return authorities;
	}

	@Override
	public boolean isEnabled() {
		return true; // 기본적으로 활성화되었다고 가정
	}

	@Override
	public boolean isAccountNonExpired() {
		return true; // 계정이 만료되지 않았다고 가정
	}

	@Override
	public boolean isAccountNonLocked() {
		return true; // 계정이 잠기지 않았다고 가정
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true; // 인증 정보가 만료되지 않았다고 가정
	}
}