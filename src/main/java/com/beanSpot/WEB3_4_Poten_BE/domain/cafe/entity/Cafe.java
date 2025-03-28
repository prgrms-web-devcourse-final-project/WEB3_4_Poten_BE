package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity;

import java.time.LocalDateTime;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
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

	@NotNull
	private Long ownerId;

	private String name;

	private String address;

	private Double latitude;

	private Double longitude;

	private String phone;

	@Column(columnDefinition = "TEXT")
	private String description;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private String image;

	private Boolean disabled;

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
		this.updatedAt = LocalDateTime.now(); // 업데이트 시간 갱신
	}
}


