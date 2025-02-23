package com.java.school.online_video_training.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileStorageConfig {
	  @Value("${file.upload-dir:${user.home}/app-uploads}")
	    private String uploadDir;
	    
	    @PostConstruct
	    public void initialize() {
	        try {
	            Path path = Paths.get(uploadDir);
	            if (!Files.exists(path)) {
	                Files.createDirectories(path);
	                // Set directory permissions
	                path.toFile().setReadable(true, false);
	                path.toFile().setWritable(true, false);
	            }
	        } catch (IOException e) {
	            throw new RuntimeException("Could not create upload directory!", e);
	        }
	    }
	    
	    @Bean
	    public String uploadDirectory() {
	        return uploadDir;
	    }
}