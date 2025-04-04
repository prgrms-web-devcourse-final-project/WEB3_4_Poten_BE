package com.beanSpot.WEB3_4_Poten_BE.domain.image.controller;

import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class ImageController {

	private final ImageService imageService;

	/**
	 * 이미지 업로드 API
	 */
	@PostMapping("/upload")
	public ResponseEntity<S3Res> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		S3Res response = imageService.uploadAndSaveImage(file);
		return ResponseEntity.ok(response);
	}

	/**
	 * 업로드한 이미지 URL 조회 API
	 */
	@GetMapping("/{imageId}")
	public ResponseEntity<Image> getImage(@PathVariable Long imageId) {
		Image image = imageService.getImageById(imageId);
		return ResponseEntity.ok(image);
	}
}
