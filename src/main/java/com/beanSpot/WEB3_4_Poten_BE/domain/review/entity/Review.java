package com.beanSpot.WEB3_4_Poten_BE.domain.review.entity;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "user_id")
	private Member member;

	@ManyToOne
	@JoinColumn(name = "cafe_id")
	private Cafe cafe;

	@Column(nullable = false)
	private int rating;

	@Column(length = 1000, nullable = false) //추후 수정 가능
	private String comment;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;



	public void updateReview(int rating, String comment) {
		this.rating = rating;
		this.comment = comment;
		this.updatedAt = LocalDateTime.now();
	}
}
