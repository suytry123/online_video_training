package com.java.school.online_video_training.exception;

import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobleExceptionHandler {

	@ExceptionHandler(ApiException.class)
	public ResponseEntity<?> handleApiException(ApiException e){
		ErrorResponse errorResponse = new ErrorResponse(e.getStatus(), e.getMessage());
		return ResponseEntity
				.status(e.getStatus())
				.body(errorResponse);
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGenericException(Exception ex) {
		log.error("Unexpected error occurred", ex);
		ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
	}
	

	@ExceptionHandler(ValidationException.class)
	public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
		log.warn("Validation error: {}", ex.getMessage());
		String traceId = UUID.randomUUID().toString();
		String path = ""; // Set request path if available
		ErrorResponse errorResponse = ErrorResponse.builder()
			.success(false)
			.message(ex.getMessage())
			.status(HttpStatus.BAD_REQUEST.value())
			.path(path)
			.timestamp(java.time.LocalDateTime.now().toString())
			.traceId(traceId)
			.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
		log.warn("User already exists: {}", ex.getMessage());
		String traceId = UUID.randomUUID().toString();
		String path = ""; // Set request path if available
		ErrorResponse errorResponse = ErrorResponse.builder()
			.success(false)
			.message(ex.getMessage())
			.status(HttpStatus.CONFLICT.value())
			.path(path)
			.timestamp(java.time.LocalDateTime.now().toString())
			.traceId(traceId)
			.build();
		return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
		log.warn("Resource not found: {}", ex.getMessage());
		String traceId = UUID.randomUUID().toString();
		String path = ""; // Set request path if available
		ErrorResponse errorResponse = ErrorResponse.builder()
			.success(false)
			.message(ex.getMessage())
			.status(HttpStatus.NOT_FOUND.value())
			.path(path)
			.timestamp(java.time.LocalDateTime.now().toString())
			.traceId(traceId)
			.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
		log.warn("Method argument not valid: {}", ex.getMessage());
		String traceId = UUID.randomUUID().toString();
		String path = ""; // Set request path if available
		ErrorResponse errorResponse = ErrorResponse.builder()
			.success(false)
			.message("Validation failed")
			.status(HttpStatus.BAD_REQUEST.value())
			.path(path)
			.timestamp(java.time.LocalDateTime.now().toString())
			.traceId(traceId)
			.build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
	}
	
	 /*@ExceptionHandler(Exception.class)
	    public ResponseEntity<ApiResponse<Object>> handleAllExceptions(Exception ex, WebRequest request) {
	        String traceId = UUID.randomUUID().toString();
	        String path = request.getDescription(false).replace("uri=", "");
	        ApiResponse<Object> response = ApiResponse.error(
	            ex.getMessage(),
	            "INTERNAL_ERROR",
	            HttpStatus.INTERNAL_SERVER_ERROR,
	            path,
	            traceId
	        );
	        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	    }*/

//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
//		log.error("Unexpected error occurred", ex);
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//				.body(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR.value(), "An unexpected error occurred"));
//
//	}
}