package com.programmers.heycake.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

	private ErrorCode errorCode;

	protected BusinessException() {
	}

	public BusinessException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
