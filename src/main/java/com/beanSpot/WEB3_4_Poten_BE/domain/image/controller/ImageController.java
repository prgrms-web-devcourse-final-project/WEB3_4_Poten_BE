package com.beanSpot.WEB3_4_Poten_BE.domain.image.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.service.ImageService;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	/**
	 * 이미지 업로드 API (S3 저장 + DB 저장)
	 */
	@PostMapping("/upload")
	public ResponseEntity<S3Res> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		S3Res response = imageService.uploadAndSaveImage(file);
		return ResponseEntity.ok(response);
	}

	/**
	 * 업로드한 이미지 정보 조회
	 */
	@GetMapping("/{imageId}")
	public ResponseEntity<Image> getImage(@PathVariable Long imageId) {
		Image image = imageService.getImageById(imageId);
		return ResponseEntity.ok(image);
	}

	/**
	 * Presigned URL (다운로드용) 반환
	 */
	@GetMapping("download/{imageId}")
	public ResponseEntity<Map<String, String>> downloadImage(@PathVariable Long imageId) {
		Image image = imageService.getImageById(imageId);
		String presignedUrl = imageService.getPresignedUrl(image.getFileName());

		Map<String, String> response = new HashMap<>();
		response.put("presignedUrl", presignedUrl);

		return ResponseEntity.ok(response);
	}
}
