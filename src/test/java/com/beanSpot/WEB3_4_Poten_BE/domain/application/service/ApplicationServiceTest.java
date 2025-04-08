package com.beanSpot.WEB3_4_Poten_BE.domain.application.service;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.req.ApplicationReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.dto.res.ApplicationRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.entity.Application;
import com.beanSpot.WEB3_4_Poten_BE.domain.application.repository.ApplicationRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.exception.UserNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private ApplicationRepository applicationRepository;

	@InjectMocks
	private ApplicationService applicationService;

	@Nested
	@DisplayName("신청 생성 (createApplication)")
	class CreateApplicationTest {

		@DisplayName("성공 - 유효한 사용자와 요청 정보로 신청을 생성한다.")
		@Test
		void createApplication_success() {
			// given
			Long userId = 1L;
			ApplicationReq request = new ApplicationReq("홍길동", "서울시", "010-1234-5678");

			User user = User.builder()
				.id(userId)
				.name("홍길동")
				.build();

			when(userRepository.findById(userId)).thenReturn(Optional.of(user));

			when(applicationRepository.save(any(Application.class)))
				.thenAnswer(invocation -> {
					Application app = invocation.getArgument(0);
					return Application.builder()
						.id(100L)
						.user(app.getUser())
						.name(app.getName())
						.address(app.getAddress())
						.phone(app.getPhone())
						.status(app.getStatus())
						.createdAt(app.getCreatedAt())
						.build();
				});

			// when
			ApplicationRes result = applicationService.createApplication(request, userId);

			// then
			assertNotNull(result);
			assertEquals("홍길동", result.name());
			assertEquals("서울시", result.address());
			assertEquals("010-1234-5678", result.phone());
			assertEquals("PENDING", result.status());
			assertEquals(100L, result.id());
		}

		@DisplayName("실패 - 존재하지 않는 사용자일 경우 예외가 발생한다.")
		@Test
		void createApplication_userNotFound_throwsException() {
			// given
			Long userId = 99L;
			ApplicationReq request = new ApplicationReq("홍길동", "서울시", "010-1234-5678");

			when(userRepository.findById(userId)).thenReturn(Optional.empty());

			// when & then
			assertThrows(UserNotFoundException.class, () -> {
				applicationService.createApplication(request, userId);
			});
		}
	}
}

