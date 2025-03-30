package com.beanSpot.WEB3_4_Poten_BE.global.exceptions;

import org.springframework.http.HttpStatus;

import lombok.Getter;


@Getter
public class ServiceException extends RuntimeException{

	private final int resultCode;

	public ServiceException(String message) {
		this(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
	}

	public ServiceException(int resultCode, String message) {
		super(message);
		this.resultCode = resultCode;
	}

	public String getMessage() {
		return super.getMessage();
	}

	public String getMsg() {
		return getMessage();
	}
}