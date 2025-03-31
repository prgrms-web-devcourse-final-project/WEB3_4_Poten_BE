package com.beanSpot.WEB3_4_Poten_BE.domain.user.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.entity.Cafe;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception.CafeNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.cafe.repository.CafeRepository;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.res.UserRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.exception.UserNotFoundException;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final CafeRepository cafeRepository;

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

	@Transactional
	public List<UserRes> getUserList() {
		return userRepository.findAll().stream()
			.map(UserRes::fromEntity)
			.collect(Collectors.toList());
	}

	@Transactional
	public UserRes getUserById(Long id) {
		Optional<User> user = userRepository.findById(id);
		return user.map(UserRes::fromEntity)
			.orElseThrow(() -> new UserNotFoundException(id));
	}

	@Transactional
	public User updateUser(Long id, UserUpdateReq request) {
		User user = userRepository.findById(id)
			.orElseThrow(() -> new UserNotFoundException(id));

		user.update(request);

		return userRepository.save(user);
	}
}
