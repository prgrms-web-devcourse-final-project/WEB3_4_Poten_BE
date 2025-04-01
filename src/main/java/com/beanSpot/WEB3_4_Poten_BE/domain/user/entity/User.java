package com.beanSpot.WEB3_4_Poten_BE.domain.user.entity;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserUpdateReq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String role;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String oAuthId;

	@Enumerated(EnumType.STRING)
	private Member.SnsType snsType;

	private String profileImageName;

	public void update(UserUpdateReq request) {

		if (request.name() != null) {
			this.name = request.name();
		}
		if (request.email() != null) {
			this.email = request.email();
		}
		if (request.password() != null) {
			this.password = request.password();
		}
		if (request.email() != null) {
			this.email = request.email();
		}
		this.updatedAt = LocalDateTime.now();
	}
}
