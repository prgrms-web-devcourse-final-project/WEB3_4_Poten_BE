package com.beanSpot.WEB3_4_Poten_BE.domain.image.service;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.repository.ImageRepository;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ImageServiceTest {

	private S3Service s3Service;
	private ImageRepository imageRepository;
	private ImageService imageService;

	@BeforeEach
	void setUp() {
		s3Service = mock(S3Service.class);
		imageRepository = mock(ImageRepository.class);
		imageService = new ImageService(s3Service, imageRepository);
	}

	@Nested
	@DisplayName("이미지 수정 테스트")
	class UpdateImageTest {

		@Test
		@DisplayName("이미지 수정 성공")
		void updateImage_success() throws IOException {
			// given
			Long imageId = 1L;
			Image existingImage = Image.builder()
				.id(imageId)
				.fileName("old-file.jpg")
				.fileUrl("https://old-url")
				.fileType("image/jpeg")
				.fileSize(100L)
				.build();

			MockMultipartFile newFile = new MockMultipartFile(
				"file",
				"updated.png",
				"image/png",
				"new content".getBytes()
			);

			UUID mockUUID = UUID.randomUUID();
			String expectedFileName = mockUUID + "_" + newFile.getOriginalFilename();
			String expectedFileUrl = "https://s3-url/" + expectedFileName;

			when(imageRepository.findById(imageId)).thenReturn(Optional.of(existingImage));
			when(s3Service.uploadFile(any(), eq(expectedFileName))).thenReturn(expectedFileUrl);

			try (MockedStatic<UUID> mockedUUID = mockStatic(UUID.class)) {
				mockedUUID.when(UUID::randomUUID).thenReturn(mockUUID);

				// when
				Image updatedImage = imageService.updateImage(imageId, newFile);

				// then
				assertThat(updatedImage.getFileName()).isEqualTo(expectedFileName);
				assertThat(updatedImage.getFileUrl()).isEqualTo(expectedFileUrl);
				assertThat(updatedImage.getFileType()).isEqualTo("image/png");
				assertThat(updatedImage.getFileSize()).isEqualTo(newFile.getSize());
			}

			verify(s3Service).deleteFile("old-file.jpg");
			verify(imageRepository).findById(imageId);
		}
	}
}
