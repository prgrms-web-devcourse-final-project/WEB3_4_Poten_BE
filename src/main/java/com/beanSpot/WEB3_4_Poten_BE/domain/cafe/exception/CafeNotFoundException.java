package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception;

public class CafeNotFoundException extends RuntimeException {
	public CafeNotFoundException(Long id) {
		super("카페를 찾을 수 없습니다. ID:" + id);
	}
}