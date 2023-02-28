package com.programmers.heycake.common.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {

	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 입력 값입니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없는 사용자입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 데이터입니다."),
	DUPLICATED(HttpStatus.CONFLICT, "중복된 데이터입니다."),
	DUPLICATED_OFFER(HttpStatus.CONFLICT, "이미 해당 주문에 제안 글을 작성하였습니다."),
	VISIT_DATE_PASSED(HttpStatus.CONFLICT, "이미 날짜가 지난 주문입니다."),
	ORDER_CLOSED(HttpStatus.CONFLICT, "이미 완료된 주문입니다.");

	private final HttpStatus status;
	private final String message;

	ErrorCode(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
