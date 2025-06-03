package com.java.school.online_video_training.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.java.school.online_video_training.service.LogoService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LogoServiceImpl implements LogoService {
	@Value("${app.logo-upload-dir:file-repository}")
	private String logoUploadDir;

	@Value("${app.base-url}")
	private String baseUrl;

	@Override
	public String updateLogo(MultipartFile file) throws Exception {
		if (file == null || file.isEmpty()) {
			log.error("Logo file is empty or null");
			throw new IllegalArgumentException("Logo file is empty");
		}
		String extension = getFileExtension(file.getOriginalFilename());
		String filename = UUID.randomUUID().toString() + (extension.isEmpty() ? "" : "." + extension);
		Path uploadPath = Paths.get(logoUploadDir).toAbsolutePath();
		log.info("Uploading logo to directory: {}", uploadPath);
		if (!Files.exists(uploadPath)) {
			try {
				Files.createDirectories(uploadPath);
				log.info("Created upload directory: {}", uploadPath);
			} catch (Exception e) {
				log.error("Failed to create upload directory: {}", uploadPath, e);
				throw new RuntimeException("Could not create upload directory", e);
			}
		}
		Path filePath = uploadPath.resolve(filename);
		try {
			file.transferTo(filePath);
			log.info("Logo file saved as: {}", filePath);
		} catch (Exception e) {
			log.error("Failed to save logo file: {}", filePath, e);
			throw new RuntimeException("Failed to save logo file", e);
		}
		return baseUrl + "/file-repository/" + filename;
	}

	private String getFileExtension(String filename) {
		if (filename == null)
			return "";
		int dotIndex = filename.lastIndexOf('.');
		return (dotIndex == -1) ? "" : filename.substring(dotIndex + 1);
	}
}