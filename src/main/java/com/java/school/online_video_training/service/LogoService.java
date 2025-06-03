package com.java.school.online_video_training.service;

import org.springframework.web.multipart.MultipartFile;

public interface LogoService {
    String updateLogo(MultipartFile file) throws Exception;
}