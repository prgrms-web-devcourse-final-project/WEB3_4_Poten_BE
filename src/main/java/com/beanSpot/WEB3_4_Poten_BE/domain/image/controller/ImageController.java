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

	@PostMapping("/upload")
	public ResponseEntity<S3Res> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
		S3Res response = imageService.uploadAndSaveImage(file);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/{imageId}")
	public ResponseEntity<Image> getImage(@PathVariable Long imageId) {
		Image image = imageService.getImageById(imageId);
		return ResponseEntity.ok(image);
	}

	@GetMapping("/download/{imageId}")
	public ResponseEntity<Map<String, String>> downloadImage(@PathVariable Long imageId) {
		Image image = imageService.getImageById(imageId);
		String presignedUrl = imageService.getPresignedUrl(image.getFileName());

		Map<String, String> response = new HashMap<>();
		response.put("presignedUrl", presignedUrl);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/download-by-name/{fileName}")
	public ResponseEntity<Map<String, String>> downloadImageByFileName(@PathVariable String fileName) {
		String presignedUrl = imageService.getPresignedUrl(fileName);

		Map<String, String> response = new HashMap<>();
		response.put("presignedUrl", presignedUrl);
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{imageId}")
	public ResponseEntity<String> deleteImage(@PathVariable Long imageId) {
		imageService.deleteImage(imageId);
		return ResponseEntity.ok("이미지가 삭제되었습니다.");
	}

	// 이미지 수정
	@PutMapping("/{id}")
	public ResponseEntity<Image> updateImage(
		@PathVariable("id") Long imageId,
		@RequestParam("file") MultipartFile file
	) throws IOException {
		Image updatedImage = imageService.updateImage(imageId, file);
		return ResponseEntity.ok(updatedImage);
	}
}
