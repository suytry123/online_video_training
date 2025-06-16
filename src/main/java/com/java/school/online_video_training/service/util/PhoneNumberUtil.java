package com.java.school.online_video_training.service.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.Phonenumber;

/**
 * Utility for phone number normalization and validation.
 */
public final class PhoneNumberUtil {
    private static final com.google.i18n.phonenumbers.PhoneNumberUtil phoneUtil = com.google.i18n.phonenumbers.PhoneNumberUtil.getInstance();
    private static final String DEFAULT_REGION = "KH"; // Cambodia

    // Prevent instantiation
    private PhoneNumberUtil() {}

    /**
     * Normalize a phone number to E.164 format. Accepts local (0...) or E.164 (+855...)
     * @param input the user input phone number
     * @return E.164 formatted phone number (e.g. +85512345678)
     * @throws IllegalArgumentException if the number is invalid
     */
    public static String toE164(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is empty");
        }
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(input, DEFAULT_REGION);
            if (!phoneUtil.isValidNumber(number)) {
                throw new IllegalArgumentException("Invalid phone number: " + input);
            }
            return phoneUtil.format(number, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number: " + input);
        }
    }

    /**
     * Check if a phone number is already in E.164 format (starts with '+', valid, and matches E.164 output)
     */
    public static boolean isE164(String input) {
        if (input == null || !input.startsWith("+")) return false;
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(input, DEFAULT_REGION);
            return phoneUtil.isValidNumber(number) && input.equals(phoneUtil.format(number, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.E164));
        } catch (NumberParseException e) {
            return false;
        }
    }

    /**
     * Format a phone number to national format (e.g. 012 345 678)
     */
    public static String toNational(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is empty");
        }
        try {
            Phonenumber.PhoneNumber number = phoneUtil.parse(input, DEFAULT_REGION);
            if (!phoneUtil.isValidNumber(number)) {
                throw new IllegalArgumentException("Invalid phone number: " + input);
            }
            return phoneUtil.format(number, com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            throw new IllegalArgumentException("Invalid phone number: " + input);
        }
    }
}
