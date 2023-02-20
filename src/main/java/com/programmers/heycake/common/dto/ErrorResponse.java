package com.programmers.heycake.common.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public record ErrorResponse(
		String message,
		String path,
		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
		LocalDateTime time,
		List<FieldError> inputErrors
) {

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class FieldError {

		private String field;
		private String rejectedValue;
		private String message;

		public FieldError(String field, String rejectedValue, String message) {
			this.field = field;
			this.rejectedValue = rejectedValue;
			this.message = message;
		}
	}

	public static ErrorResponse of(String message, String path, List<FieldError> inputErrors) {
		return new ErrorResponse(message, path, LocalDateTime.now(), inputErrors);
	}
}
