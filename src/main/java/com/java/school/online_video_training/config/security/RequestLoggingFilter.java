package com.java.school.online_video_training.config.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap the request
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // Continue filter chain using wrapped request
        filterChain.doFilter(wrappedRequest, response);

        // Log the body AFTER it has been read by Spring
        byte[] buf = wrappedRequest.getContentAsByteArray();
        if (buf.length > 0) {
            String body = new String(buf, 0, buf.length, StandardCharsets.UTF_8);
            log.info("Request Body: {}", body);
        }
    }
}