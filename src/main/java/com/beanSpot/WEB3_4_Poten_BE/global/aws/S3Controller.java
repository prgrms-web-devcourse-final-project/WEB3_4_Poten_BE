package com.beanSpot.WEB3_4_Poten_BE.global.aws;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class S3Controller {

	private final S3Service s3Service;

	@PostMapping("/upload")
	public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			String fileName = s3Service.uploadFile(file);
			String fileUrl = s3Service.getFileUrl(fileName);

			Map<String, String> response = new HashMap<>();
			response.put("fileName", fileName);
			response.put("fileUrl", fileUrl);

			return ResponseEntity.ok(response);
		} catch (IOException e) {
			Map<String, String> errorResponse = new HashMap<>();
			errorResponse.put("error", "파일 업로드 실패: " + e.getMessage());
			return ResponseEntity.badRequest().body(errorResponse);
		}
	}

	@GetMapping("/{fileName}")
	public ResponseEntity<Map<String, String>> getFileUrl(@PathVariable String fileName) {
		String presignedUrl = s3Service.getPresignedUrl(fileName);

		Map<String, String> response = new HashMap<>();
		response.put("fileUrl", presignedUrl);

		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/{fileName}")
	public ResponseEntity<Map<String, String>> deleteFile(@PathVariable String fileName) {
		s3Service.deleteFile(fileName);

		Map<String, String> response = new HashMap<>();
		response.put("message", "파일이 성공적으로 삭제되었습니다.");

		return ResponseEntity.ok(response);
	}
}
