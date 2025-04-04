package com.beanSpot.WEB3_4_Poten_BE.domain.member.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.review.entity.Review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
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
@EntityListeners(AuditingEntityListener.class)
public class Member implements UserDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String email;

	private String username;

	private String password;

	private String profileImg;

	private String oAuthId;

	private String phoneNumber;

	@Enumerated(EnumType.STRING)
	private MemberType memberType;

	@Enumerated(EnumType.STRING)
	private SnsType snsType;

	@CreatedDate
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime updatedAt;

	@OneToOne(mappedBy = "member")
	private Application application;

	@OneToMany(mappedBy = "member")
	private List<Review> reviews = new ArrayList<>();

	// @OneToMany(mappedBy = "member")
	// private List<Reservation> reservations = new ArrayList<>();
	@OneToMany(mappedBy = "owner")
	private List<Cafe> ownedCafes = new ArrayList<>();

	public enum MemberType {
		USER(Collections.singletonList("ROLE_USER")),
		ADMIN(Arrays.asList("ROLE_ADMIN")),
		OWNER(Arrays.asList("ROLE_USER", "ROLE_OWNER"));

		private final List<String> roles;

		MemberType(List<String> roles) {
			this.roles = roles;
		}

		public List<String> getRoles() {
			return roles;
		}
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.memberType.getRoles().stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());
	}

	public enum SnsType {
		KAKAO, NAVER, GOOGLE
	}


	// 역할 변경 메서드
	public void changeRoleToOwner() {
		this.memberType = MemberType.OWNER;
		// updatedAt은 @LastModifiedDate 어노테이션으로 자동 관리됨
	}

	@Override
	public String getUsername() {
		return this.oAuthId != null ? this.oAuthId : this.username;
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