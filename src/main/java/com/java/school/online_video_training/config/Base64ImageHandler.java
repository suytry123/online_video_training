package com.java.school.online_video_training.config;

import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;

public class Base64ImageHandler {
    
    // Pattern to validate Base64 string
    private static final Pattern BASE64_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");

    public byte[] decodeImage(String base64String) throws IllegalArgumentException {
        try {
            // Clean the string
            String cleanBase64 = base64String.trim()
                    .replaceAll("\\s", "")     // Remove whitespace
                    .replaceAll("\\.+", "")    // Remove dots
                    .replaceAll("/{2,}", "/"); // Replace multiple slashes with single slash

            // Validate the cleaned string
            if (!BASE64_PATTERN.matcher(cleanBase64).matches()) {
                throw new IllegalArgumentException("Invalid Base64 string format");
            }

            // Decode
            return Base64.decodeBase64(cleanBase64);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to decode Base64 string: " + e.getMessage());
        }
    }

    public String encodeImage(byte[] imageBytes) {
        try {
            return Base64.encodeBase64String(imageBytes);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to encode image bytes: " + e.getMessage());
        }
    }

    // Validate if a string is proper Base64
    public boolean isValidBase64(String base64String) {
        if (base64String == null || base64String.trim().isEmpty()) {
            return false;
        }
        
        String cleaned = base64String.trim()
                .replaceAll("\\s", "")
                .replaceAll("\\.+", "")
                .replaceAll("/{2,}", "/");
                
        return BASE64_PATTERN.matcher(cleaned).matches();
    }
}
