package com.java.school.online_video_training.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class loadFileResource {
	@Autowired
	private ServletContext context;
	
	public Resource loadFileResource(String path) {
	        try {
	            String absolutePath = context.getRealPath("resources/file-repository");
	            Path filePath = Paths.get(absolutePath).resolve(path).normalize();
	            Resource resource = new UrlResource(filePath.toUri());

	            if (resource.exists() && resource.isReadable()) {
	                return resource;
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
}
