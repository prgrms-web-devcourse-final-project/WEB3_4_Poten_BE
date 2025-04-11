package com.beanSpot.WEB3_4_Poten_BE.domain.image.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.repository.ImageRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

	@Mock
	private S3Service s3Service;

	@Mock
	private ImageRepository imageRepository;

	@InjectMocks
	private ImageService imageService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("이미지 업로드 성공")
	void uploadImage_success() throws IOException {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file", "test-image.jpg", "image/jpeg", "image content".getBytes());

		String fileName = "abc123-test-image.jpg";
		String fileUrl = "https://s3.example.com/" + fileName;

		when(s3Service.uploadFile(file)).thenReturn(fileName);
		when(s3Service.getFileUrl(fileName)).thenReturn(fileUrl);
		when(imageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		// when
		var response = imageService.uploadAndSaveImage(file);

		// then
		assertThat(response.getFileName()).isEqualTo(fileName);
		assertThat(response.getFileUrl()).isEqualTo(fileUrl);
		verify(imageRepository, times(1)).save(any(Image.class));
	}

	@Test
	@DisplayName("이미지 조회 성공")
	void getImage_success() {
		// given
		Image image = Image.builder()
			.id(1L)
			.fileName("img.jpg")
			.fileUrl("http://s3/image.jpg")
			.fileType("image/jpeg")
			.fileSize(12345L)
			.build();

		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

		// when
		Image result = imageService.getImageById(1L);

		// then
		assertThat(result.getFileName()).isEqualTo("img.jpg");
	}

	@Test
	@DisplayName("이미지 삭제 성공")
	void deleteImage_success() {
		// given
		Image image = Image.builder()
			.id(1L)
			.fileName("img.jpg")
			.build();

		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));

		// when
		imageService.deleteImage(1L);

		// then
		verify(s3Service, times(1)).deleteFile("img.jpg");
		verify(imageRepository, times(1)).delete(image);
	}

	@Test
	@DisplayName("이미지 수정 성공")
	void updateImage_success() throws IOException {
		// given
		MockMultipartFile newFile = new MockMultipartFile(
			"file", "updated.png", "image/png", "new data".getBytes());

		Image image = Image.builder()
			.id(1L)
			.fileName("old.jpg")
			.build();

		String newFileName = "uuid-updated.png";
		String newUrl = "http://s3/uuid-updated.png";

		when(imageRepository.findById(1L)).thenReturn(Optional.of(image));
		when(s3Service.uploadFile(eq(newFile), anyString())).thenReturn(newFileName);
		when(s3Service.getFileUrl(newFileName)).thenReturn(newUrl);

		// when
		Image updated = imageService.updateImage(1L, newFile);

		// then
		assertThat(updated.getFileName()).isEqualTo(newFileName);
		assertThat(updated.getFileUrl()).isEqualTo(newUrl);
	}
}

