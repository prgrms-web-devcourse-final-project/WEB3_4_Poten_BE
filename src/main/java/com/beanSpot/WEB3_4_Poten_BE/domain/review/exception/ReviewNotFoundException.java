package com.beanSpot.WEB3_4_Poten_BE.domain.review.exception;

public class ReviewNotFoundException extends RuntimeException {
	public ReviewNotFoundException(Long id) {
		super("리뷰를 찾을 수 없습니다. ID:" + id);
	}
}