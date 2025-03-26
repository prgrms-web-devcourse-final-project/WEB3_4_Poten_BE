package com.beanSpot.WEB3_4_Poten_BE.domain.member.entity;

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

public class Member {
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

	public enum MemberType {
		USER, ADMIN, OWNER
	}

	public enum SnsType {
		KAKAO, NAVER
	}
}
