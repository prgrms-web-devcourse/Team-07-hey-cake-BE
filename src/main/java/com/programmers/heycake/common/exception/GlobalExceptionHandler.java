package com.programmers.heycake.common.exception;

import static com.programmers.heycake.common.exception.ErrorCode.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;

import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.programmers.heycake.common.dto.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
			HttpServletRequest request, IllegalArgumentException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(ErrorResponse.of(BAD_REQUEST.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
			HttpServletRequest request, MethodArgumentNotValidException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(
						ErrorResponse.of(
								BAD_REQUEST.getMessage(),
								request.getRequestURI(),
								makeFieldErrorsFromBindingResult(e.getBindingResult())
						)
				);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	protected ResponseEntity<ErrorResponse> handleConstraintViolationException(
			HttpServletRequest request, ConstraintViolationException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
				.body(ErrorResponse.of(
						BAD_REQUEST.getMessage(),
						request.getRequestURI()
						, makeFieldErrorsFromConstraintViolations(e.getConstraintViolations())
				));
	}

	@ExceptionHandler(BindException.class)
	public ResponseEntity<ErrorResponse> handleBindException(
			HttpServletRequest request, BindException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(
						ErrorResponse.of(
								BAD_REQUEST.getMessage(),
								request.getRequestURI(),
								makeFieldErrorsFromBindingResult(e.getBindingResult())
						)
				);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(
			HttpServletRequest request, MethodArgumentTypeMismatchException e
	) {
		logInfo(e, request.getRequestURI());

		ErrorResponse errorResponse = ErrorResponse.of(
				e.getMessage(),
				request.getRequestURI(),
				List.of(new ErrorResponse.FieldError(
						e.getName(),
						Objects.requireNonNull(e.getValue()).toString(),
						e.getParameter().getParameterName()
				))
		);
		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(InvalidFormatException.class)
	protected ResponseEntity<ErrorResponse> handleInvalidFormatException(
			HttpServletRequest request, InvalidFormatException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
				.body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(NullPointerException.class)
	protected ResponseEntity<ErrorResponse> handleNullPointerException(
			HttpServletRequest request, NullPointerException e
	) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity.badRequest()
				.body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ErrorResponse> handleBusinessException(HttpServletRequest request, BusinessException e) {
		logInfo(e, request.getRequestURI());

		return ResponseEntity
				.status(e.getErrorCode().getStatus())
				.body(ErrorResponse.of(e.getErrorCode().getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDeniedException(HttpServletRequest request, RuntimeException e) {
		logInfo(e, request.getRequestURI());
		throw new AccessDeniedException(e.getMessage());
	}


	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(HttpServletRequest request, RuntimeException e) {
		logWarn(e, request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleRuntimeException(HttpServletRequest request, Exception e) {
		logError(e, request.getRequestURI());

		return ResponseEntity
				.badRequest()
				.body(ErrorResponse.of(e.getMessage(), request.getRequestURI(), null));
	}

	private List<ErrorResponse.FieldError> makeFieldErrorsFromBindingResult(BindingResult bindingResult) {

		return bindingResult.getFieldErrors()
				.stream()
				.map(error -> new ErrorResponse.FieldError(
						error.getField(),
						error.getRejectedValue(),
						error.getDefaultMessage()
				))
				.toList();
	}

	private List<ErrorResponse.FieldError> makeFieldErrorsFromConstraintViolations(
			Set<ConstraintViolation<?>> constraintViolations
	) {
		return constraintViolations.stream()
				.map(violation -> new ErrorResponse.FieldError(
						getFieldFromPath(violation.getPropertyPath()),
						violation.getInvalidValue().toString(),
						violation.getMessage()
				))
				.toList();
	}

	private String getFieldFromPath(Path fieldPath) {
		PathImpl pathImpl = (PathImpl)fieldPath;
		return pathImpl.getLeafNode().toString();
	}

	private void logInfo(Exception e, String url) {
		log.info("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
	}

	private void logWarn(Exception e, String url) {
		log.info("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
	}

	private void logError(Exception e, String url) {
		log.info("URL = {}, Exception = {}, Message = {}", url, e.getClass().getSimpleName(), e.getMessage());
	}
}
