/*
package com.beanSpot.WEB3_4_Poten_BE.domain.user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserCreateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.req.UserUpdateReq;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.dto.res.UserRes;
import com.beanSpot.WEB3_4_Poten_BE.domain.user.entity.User;
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

	@GetMapping
	public List<UserRes> getUserList() {
		return userService.getUserList();
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserRes> getUser(@PathVariable Long id) {
		UserRes userRes = userService.getUserById(id);
		return new ResponseEntity<>(userRes, HttpStatus.OK);
	}

	@PutMapping("/{id}")
	public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateReq request) {
		User updatedUser = userService.updateUser(id, request);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		userService.deleteUser(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
*/
