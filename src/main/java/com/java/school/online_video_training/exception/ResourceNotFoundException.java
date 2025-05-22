package com.java.school.online_video_training.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException{

	
	public ResourceNotFoundException(String resourceName, Long id) {
		//String text = String.format("%s With id = %d Not Found", resourceName, id);
		super(HttpStatus.NOT_FOUND, String.format("%s With id = %d Not Found", resourceName, id));
		
	}
	
	 public ResourceNotFoundException(String resourceName, String path) {
	        super(HttpStatus.NOT_FOUND, String.format("%s With path = %s Not Found", resourceName, path));
	        //this.status = HttpStatus.NOT_FOUND;
	    }
	 
	 public ResourceNotFoundException(String resourceName) {
			//String text = String.format("%s With id = %d Not Found", resourceName, id);
//			super(HttpStatus.NOT_FOUND, String.format(resourceName));
		 super(HttpStatus.NOT_FOUND, String.format("%s Not Found", resourceName));
			
		}
}
