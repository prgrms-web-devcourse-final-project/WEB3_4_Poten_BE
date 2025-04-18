package com.beanSpot.WEB3_4_Poten_BE.global.aws;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class S3Res {
	private String fileName;
	private String fileUrl;
	private String fileType;
	private long fileSize;
}
