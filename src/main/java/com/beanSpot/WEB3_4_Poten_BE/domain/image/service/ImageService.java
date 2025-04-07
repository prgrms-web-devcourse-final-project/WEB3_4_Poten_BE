package com.beanSpot.WEB3_4_Poten_BE.domain.image.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.repository.ImageRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final S3Service s3Service;
	private final ImageRepository imageRepository;

	/**
	 * 이미지 업로드 후 S3 저장 & DB 저장
	 */
	@Transactional
	public S3Res uploadAndSaveImage(MultipartFile file) throws IOException {
		// 1. S3에 파일 업로드
		String fileName = s3Service.uploadFile(file);
		String fileUrl = s3Service.getFileUrl(fileName);

		// 2. 응답 객체 생성
		S3Res s3Res = S3Res.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		// 3. DB에 이미지 정보 저장
		Image image = Image.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		imageRepository.save(image);
		imageRepository.flush();

		return s3Res;
	}

	/**
	 * 이미지 ID로 조회
	 */
	public Image getImageById(Long imageId) {
		return imageRepository.findById(imageId)
			.orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다. ID: " + imageId));
	}

	/**
	 * presigned url 발급
	 */
	public String getPresignedUrl(String fileName) {
		return s3Service.getPresignedUrl(fileName);
	}
}
