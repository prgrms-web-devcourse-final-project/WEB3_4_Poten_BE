package com.beanSpot.WEB3_4_Poten_BE.domain.cafe.exception;

import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

public class CafeNotFoundException extends ServiceException {
	public CafeNotFoundException(Long id) {
		super(404, "카페를 찾을 수 없습니다. ID:" + id);
	}
}