package com.careernest.common.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class HashingUtil {
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
    
    private HashingUtil() {
    }

    public static String sha256(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(raw.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available", ex);
        }
    }

    public static String bcrypt(String raw) {
        return passwordEncoder.encode(raw);
    }

    public static boolean bcryptMatches(String raw, String encoded) {
        return passwordEncoder.matches(raw, encoded);
    }
}
