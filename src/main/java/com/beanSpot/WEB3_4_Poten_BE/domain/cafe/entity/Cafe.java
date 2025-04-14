package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.member.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Cafe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cafeId;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Member owner;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "application_id", nullable = false, unique = true)
	private Application application;

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
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "image_filename", nullable = true)
	private String imageFilename;

	@Column(nullable = false)
	private int capacity;

	@Column(nullable = false)
	private boolean disabled = false;

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
		if (request.description() != null) {
			this.capacity = request.capacity();
		}
		if (request.imageFilename() != null) {
			this.imageFilename = request.imageFilename();
		}
    
		this.updatedAt = LocalDateTime.now();
	}

	public void disable() {
		this.disabled = true;
		this.updatedAt = LocalDateTime.now();
	}
}


