package com.programmers.heycake.common.exception;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.programmers.heycake.common.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> badRequestHandler(HttpServletRequest request) {
		return ResponseEntity
				.badRequest()
				.body(ErrorResponse.of(BAD_REQUEST.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> badRequestHandler(HttpServletRequest request,
			MethodArgumentNotValidException e) {
		return ResponseEntity
				.badRequest()
				.body(
						ErrorResponse.of(
								BAD_REQUEST.getMessage(),
								request.getRequestURI(),
								getFieldErrors(e.getBindingResult())
						)
				);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> runtimeExceptionHandler(HttpServletRequest request, BusinessException e) {
		return ResponseEntity
				.status(e.getErrorCode().getStatus())
				.body(ErrorResponse.of(e.getErrorCode().getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> runtimeExceptionHandler(HttpServletRequest request, RuntimeException e) {
		return ResponseEntity
				.badRequest()
				.body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
	}

	private List<ErrorResponse.FieldError> getFieldErrors(BindingResult bindingResult) {

		List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();

		for (FieldError error : bindingResult.getFieldErrors()) {
			ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError(
					error.getField(),
					error.getRejectedValue().toString(),
					error.getDefaultMessage());

			fieldErrors.add(fieldError);
		}

		return fieldErrors;
	}
}
