package com.beanSpot.WEB3_4_Poten_BE.domain.application.exception;

public class ApplicationNotFoundException extends RuntimeException {
	public ApplicationNotFoundException(Long id) {
		super("신청을 찾을 수 없습니다. ID:" + id);
	}
}
