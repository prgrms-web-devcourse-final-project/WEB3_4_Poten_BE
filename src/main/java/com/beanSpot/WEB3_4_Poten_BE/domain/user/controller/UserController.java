package com.beanSpot.WEB3_4_Poten_BE.domain.user.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.res.UserRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserRes> createUser(@Valid @RequestBody UserCreateReq request
	) {
		UserRes userRes = userService.createUser(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(userRes);
	}

}
