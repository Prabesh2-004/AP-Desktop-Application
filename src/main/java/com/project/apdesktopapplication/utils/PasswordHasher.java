package com.project.apdesktopapplication.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

// One-way password hashing (SHA-256 + static salt) so raw passwords are never
// written to disk or compared in plain text. Call hash() when storing a new/updated
// password, and matches() when checking a login attempt against the stored hash.
public class PasswordHasher {

    private static final String SALT = "CRSBS_2026_SALT";

    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest((SALT + rawPassword).getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    public static boolean matches(String rawPassword, String storedHash) {
        return hash(rawPassword).equals(storedHash);
    }
}