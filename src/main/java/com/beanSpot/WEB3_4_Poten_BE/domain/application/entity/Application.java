package com.beanSpot.WEB3_4_Poten_BE.domain.application.entity;

import java.time.LocalDateTime;


import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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
public class Application {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/*@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;*/

	@OneToOne(mappedBy = "application")
	private Cafe cafe;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private String phone;

	@Enumerated(EnumType.STRING)
	private Status status = Status.PENDING;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@OneToOne
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	public void approve() {
		this.status = Status.APPROVED;
	}

	public void reject() {
		this.status = Status.REJECTED;
	}
}
