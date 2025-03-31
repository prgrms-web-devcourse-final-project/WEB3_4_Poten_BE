package com.beanSpot.WEB3_4_Poten_BE.domain.review.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Column(name = "user_id", nullable = false)
	private Long userId; // 사용자 ID

	@Column(name = "cafe_id", nullable = false)
	private Long cafeId; // 카페 ID

	@Column(nullable = false)
	private int rating; // 평점

	@Column(length = 1000) //추후 수정 가능
	private String comment;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	public void updateReview(int rating, String comment) {
		this.rating = rating;
		this.comment = comment;
		this.updatedAt = LocalDateTime.now();
	}
}
