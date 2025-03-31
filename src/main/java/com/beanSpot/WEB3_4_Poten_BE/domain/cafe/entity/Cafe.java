package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.dto.req.CafeUpdateReq;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Cafe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cafeId;

//	@NotNull
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

	@Column(columnDefinition = "TEXT")
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


