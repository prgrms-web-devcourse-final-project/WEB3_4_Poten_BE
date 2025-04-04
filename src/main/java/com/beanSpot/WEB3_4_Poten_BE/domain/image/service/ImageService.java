package com.beanSpot.WEB3_4_Poten_BE.domain.image.service;

import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.repository.ImageRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ImageService {

	private final S3Service s3Service;
	private final ImageRepository imageRepository;

	public S3Res uploadAndSaveImage(MultipartFile file) throws IOException {
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
}
