package com.beanSpot.WEB3_4_Poten_BE.domain.user.exception;

public class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(Long id) {
		super("신청을 찾을 수 없습니다. ID:" + id);
	}
}
