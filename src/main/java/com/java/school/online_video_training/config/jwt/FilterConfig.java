package com.java.school.online_video_training.config.jwt;

import javax.servlet.Filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.java.school.online_video_training.config.security.RequestLoggingFilter;

@Configuration
public class FilterConfig {

    @Bean
    public Filter requestLoggingFilter() {
        return new RequestLoggingFilter();
    }
}