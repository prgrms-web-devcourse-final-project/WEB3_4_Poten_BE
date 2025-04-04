package com.beanSpot.WEB3_4_Poten_BE.domain.image.service;

import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.repository.ImageRepository;
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
	 * 이미지 업로드 후 S3에 저장 & DB에 저장
	 */
	@Transactional // 트랜잭션 적용
	public S3Res uploadAndSaveImage(MultipartFile file) throws IOException {
		// 1️⃣ S3에 파일 업로드
		String fileName = s3Service.uploadFile(file);
		String fileUrl = s3Service.getFileUrl(fileName);

		// 2️⃣ DTO 생성
		S3Res s3Res = S3Res.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		// 3️⃣ Entity 저장
		Image image = Image.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		imageRepository.save(image);
		imageRepository.flush(); // 👈 즉시 DB 반영!

		return s3Res;
	}

	/**
	 * 업로드한 이미지 URL 조회
	 */
	public Image getImageById(Long imageId) {
		return imageRepository.findById(imageId)
			.orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다. ID: " + imageId));
	}
}
