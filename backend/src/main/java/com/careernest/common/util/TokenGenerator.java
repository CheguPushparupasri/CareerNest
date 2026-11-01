package com.careernest.common.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class TokenGenerator {
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private TokenGenerator() {
    }

    public static String opaqueToken() {
        byte[] bytes = new byte[48];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
