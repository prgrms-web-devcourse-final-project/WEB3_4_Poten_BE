package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;

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
public class Cafe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cafeId;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private User owner;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String address;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(nullable = false)
	private String phone;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(nullable = false)
	private String image;

	@Column(nullable = false)
	private Boolean disabled;

	@Column(nullable = false)
	private int capacity;

	private boolean deleted;

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public void update(CafeUpdateReq request) {
		if (request.name() != null) {
			this.name = request.name();
		}
		if (request.address() != null) {
			this.address = request.address();
		}
		if (request.phone() != null) {
			this.phone = request.phone();
		}
		if (request.description() != null) {
			this.description = request.description();
		}
		if (request.image() != null) {
			this.image = request.image();
		}
    
		this.updatedAt = LocalDateTime.now();
	}
}


