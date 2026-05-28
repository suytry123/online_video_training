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
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest =
                new ContentCachingRequestWrapper(request);

        filterChain.doFilter(wrappedRequest, response);

        String uri = request.getRequestURI();

        // Skip auth endpoints
        if (uri.contains("/login") || uri.contains("/auth") || uri.contains("/signup_user")){
            log.info("Sensitive request skipped: {}", uri);
            return;
        }

        byte[] buf = wrappedRequest.getContentAsByteArray();

        if (buf.length > 0) {
            String body = new String(buf, StandardCharsets.UTF_8);

            // Hide password if exists
            body = body.replaceAll(
                    "\"password\":\".*?\"",
                    "\"password\":\"******\""
            );

            log.info("Request Body: {}", body);
        }
    }
}