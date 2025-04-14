package com.beanSpot.WEB3_4_Poten_BE.domain.review.exception;

import com.beanSpot.WEB3_4_Poten_BE.global.exceptions.ServiceException;

public class ReviewNotFoundException extends ServiceException {
	public ReviewNotFoundException(Long id) {
		super(404, "리뷰를 찾을 수 없습니다. ID:" + id);
	}
}