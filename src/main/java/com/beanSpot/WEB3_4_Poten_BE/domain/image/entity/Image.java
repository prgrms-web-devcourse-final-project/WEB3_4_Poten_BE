package com.beanSpot.WEB3_4_Poten_BE.domain.image.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String fileName;

	@Column(length = 1000)
	private String fileUrl;

	private String fileType;

	private long fileSize;
}

