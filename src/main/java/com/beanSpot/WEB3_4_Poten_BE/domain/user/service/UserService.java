package com.beanSpot.WEB3_4_Poten_BE.domain.user.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.res.UserRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	@Transactional
	public UserRes createUser(UserCreateReq request) {
		User user = User.builder()
			.name(request.name())
			.email(request.email())
			.password(request.password())
			.role(request.role())
			.createdAt(LocalDateTime.now())
			.updatedAt(LocalDateTime.now())
			.build();

		User savedUser = userRepository.save(user);
		return UserRes.fromEntity(savedUser);

	}
}
