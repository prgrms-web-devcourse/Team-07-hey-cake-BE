package com.programmers.heycake.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

public record ErrorResponse(
		String message,
		String path,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime time,
		List<FieldError> inputErrors
) {

	public record FieldError(String field, Object rejectedValue, String message) {
	}

	public static ErrorResponse of(String message, String path, List<FieldError> inputErrors) {
		return new ErrorResponse(message, path, LocalDateTime.now(), inputErrors);
	}
}
