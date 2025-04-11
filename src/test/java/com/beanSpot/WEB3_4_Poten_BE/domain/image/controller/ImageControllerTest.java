package com.beanSpot.WEB3_4_Poten_BE.domain.image.controller;

import com.beanSpot.WEB3_4_Poten_BE.domain.image.entity.Image;
import com.beanSpot.WEB3_4_Poten_BE.domain.image.service.ImageService;
import com.beanSpot.WEB3_4_Poten_BE.global.aws.S3Res;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ImageControllerTest {

	private MockMvc mockMvc;

	@Mock
	private ImageService imageService;

	@InjectMocks
	private ImageController imageController;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);

		CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
		encodingFilter.setEncoding("UTF-8");
		encodingFilter.setForceEncoding(true);

		mockMvc = MockMvcBuilders.standaloneSetup(imageController)
			.addFilter(encodingFilter)
			.build();
	}

	@Test
	@DisplayName("이미지 업로드 성공")
	void uploadImage_success() throws Exception {
		// given
		MockMultipartFile file = new MockMultipartFile(
			"file",
			"test.jpg",
			"image/jpeg",
			"file content".getBytes()
		);

		S3Res res = S3Res.builder()
			.fileName("uuid-test.jpg")
			.fileUrl("https://s3.amazonaws.com/bucket/uuid-test.jpg")
			.fileType("image/jpeg")
			.fileSize(12345L)
			.build();

		when(imageService.uploadAndSaveImage(any())).thenReturn(res);

		// when & then
		mockMvc.perform(multipart("/api/images/upload").file(file))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fileName").value("uuid-test.jpg"));
	}

	@Test
	@DisplayName("이미지 조회 성공")
	void getImage_success() throws Exception {
		// given
		Image image = Image.builder()
			.id(1L)
			.fileName("uuid-test.jpg")
			.fileUrl("https://s3.amazonaws.com/bucket/uuid-test.jpg")
			.fileType("image/jpeg")
			.fileSize(12345L)
			.build();

		when(imageService.getImageById(1L)).thenReturn(image);

		// when & then
		mockMvc.perform(get("/api/images/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.fileName").value("uuid-test.jpg"));
	}

	@Test
	@DisplayName("Presigned URL 다운로드 성공")
	void downloadImage_success() throws Exception {
		// given
		when(imageService.getImageById(1L)).thenReturn(Image.builder()
			.fileName("uuid-test.jpg")
			.build());

		when(imageService.getPresignedUrl("uuid-test.jpg"))
			.thenReturn("https://s3.amazonaws.com/presigned-url");

		// when & then
		mockMvc.perform(get("/api/images/download/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.presignedUrl").value("https://s3.amazonaws.com/presigned-url"));
	}

	@Test
	@DisplayName("이미지 삭제 성공")
	void deleteImage_success() throws Exception {
		// given
		doNothing().when(imageService).deleteImage(1L);

		// when & then
		mockMvc.perform(delete("/api/images/1")
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			// UTF-8 인코딩으로 비교
			.andExpect(content().bytes("이미지가 삭제되었습니다.".getBytes(StandardCharsets.UTF_8)));
	}
}
