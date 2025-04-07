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
import java.util.List;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final S3Service s3Service;
	private final ImageRepository imageRepository;

	private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/jpg");
	private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

	@Transactional
	public S3Res uploadAndSaveImage(MultipartFile file) throws IOException {
		validateFile(file);

		String fileName = s3Service.uploadFile(file);
		String fileUrl = s3Service.getFileUrl(fileName);

		S3Res s3Res = S3Res.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		Image image = Image.builder()
			.fileName(fileName)
			.fileUrl(fileUrl)
			.fileType(file.getContentType())
			.fileSize(file.getSize())
			.build();

		imageRepository.save(image);
		return s3Res;
	}

	private void validateFile(MultipartFile file) {
		if (file.isEmpty()) {
			throw new IllegalArgumentException("파일이 비어 있습니다.");
		}
		if (!ALLOWED_TYPES.contains(file.getContentType())) {
			throw new IllegalArgumentException("허용되지 않는 파일 형식입니다.");
		}
		if (file.getSize() > MAX_FILE_SIZE) {
			throw new IllegalArgumentException("파일 크기는 10MB 이하만 허용됩니다.");
		}
	}

	public Image getImageById(Long imageId) {
		return imageRepository.findById(imageId)
			.orElseThrow(() -> new RuntimeException("이미지를 찾을 수 없습니다. ID: " + imageId));
	}

	public String getPresignedUrl(String fileName) {
		return s3Service.generatePresignedUrl(fileName);
	}

	public void deleteImage(Long imageId) {
		Image image = getImageById(imageId);
		s3Service.deleteFile(image.getFileName());
		imageRepository.delete(image);
	}
}
