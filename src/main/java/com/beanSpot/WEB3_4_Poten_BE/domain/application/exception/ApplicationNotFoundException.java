package com.beanSpot.WEB3_4_Poten_BE.domain.application.exception;

import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

public class ApplicationNotFoundException extends ServiceException {
	public ApplicationNotFoundException(Long id) {
		super(404, "신청을 찾을 수 없습니다. ID:" + id);
	}
}
