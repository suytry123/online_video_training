package com.java.school.online_video_training.exception;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.security.sasl.AuthenticationException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
	public ResponseEntity<ErrorResponse> handleAuthenticationException(Exception ex) {
		log.warn("Authentication failed: {}", ex.getMessage());
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.UNAUTHORIZED.value(), "Invalid username or password");
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
	}

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
		log.warn("API Exception: {}", ex.getMessage());

		return ResponseEntity.status(ex.getStatus()).body(buildErrorResponse(ex.getStatus(), ex.getMessage(), request));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {

		log.error("Unexpected error occurred", ex);

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request));
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex, HttpServletRequest request) {

		return ResponseEntity.status(HttpStatus.LOCKED)
				.body(buildErrorResponse(HttpStatus.LOCKED, ex.getMessage(), request));
	}

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex, HttpServletRequest request) {

		log.warn("Validation error: {}", ex.getMessage());

		return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex,
			HttpServletRequest request) {

		log.warn("User already exists: {}", ex.getMessage());

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), request));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex,
			HttpServletRequest request) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex,
			HttpServletRequest request) {

		String message = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).collect(Collectors.joining(", "));

		return ResponseEntity.badRequest().body(buildErrorResponse(HttpStatus.BAD_REQUEST, message, request));
	}

	private ErrorResponse buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
		return ErrorResponse.builder().success(false).message(message).status(status.value())
				.path(request.getRequestURI()).timestamp(LocalDateTime.now().toString())
				.traceId(UUID.randomUUID().toString()).build();
	}

	/*
	 * @ExceptionHandler(Exception.class) public ResponseEntity<ApiResponse<Object>>
	 * handleAllExceptions(Exception ex, WebRequest request) { String traceId =
	 * UUID.randomUUID().toString(); String path =
	 * request.getDescription(false).replace("uri=", ""); ApiResponse<Object>
	 * response = ApiResponse.error( ex.getMessage(), "INTERNAL_ERROR",
	 * HttpStatus.INTERNAL_SERVER_ERROR, path, traceId ); return new
	 * ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); }
	 */

//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//		log.error("Unexpected error occurred", ex);
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//				.body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
//
//	}
}