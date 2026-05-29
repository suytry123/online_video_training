package com.java.school.online_video_training.exception;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ApiException extends RuntimeException {
	private final HttpStatus status;

//	private final String message;
	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}
}
