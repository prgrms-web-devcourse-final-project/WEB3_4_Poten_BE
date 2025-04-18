package com.beanSpot.WEB3_4_Poten_BE.global.aws;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3Service {

	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	private final String bucketName;

	public S3Service(S3Client s3Client,
		S3Presigner s3Presigner,
		@Value("${cloud.aws.s3.bucket-name}") String bucketName) {
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
		this.bucketName = bucketName;
	}

	public String uploadFile(MultipartFile file) throws IOException {
		String fileName = generateFileName(file);
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.contentType(file.getContentType())
			.build();

		s3Client.putObject(putObjectRequest,
			RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

		return fileName;
	}

	public String uploadFile(MultipartFile file, String customFileName) throws IOException {
		PutObjectRequest putObjectRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(customFileName)
			.contentType(file.getContentType())
			.build();

		s3Client.putObject(putObjectRequest,
			RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

		return customFileName;
	}

	public String getFileUrl(String fileName) {
		return s3Client.utilities().getUrl(GetUrlRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build()).toString();
	}

	public String generatePresignedUrl(String fileName) {
		GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
			.signatureDuration(Duration.ofMinutes(10))
			.getObjectRequest(req -> req.bucket(bucketName).key(fileName))
			.build();

		PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(getObjectPresignRequest);
		return presignedRequest.url().toString();
	}

	public void deleteFile(String fileName) {
		DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
			.bucket(bucketName)
			.key(fileName)
			.build();
		s3Client.deleteObject(deleteObjectRequest);
	}

	private String generateFileName(MultipartFile file) {
		return UUID.randomUUID() + "-" + file.getOriginalFilename();
	}
}
