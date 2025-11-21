package com.xuno.payment.payment.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class ReferenceNumberGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int REFERENCE_LENGTH = 16;
    private static final String TXN_PREFIX = "TXN";
    private static final String SND_PREFIX = "SND";
    private static final String RCV_PREFIX = "RCV";

    private ReferenceNumberGenerator() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static String generate() {
        return generateReference(TXN_PREFIX);
    }

    public static String generateSenderReference() {
        return generateReference(SND_PREFIX);
    }

    public static String generateReceiverReference() {
        return generateReference(RCV_PREFIX);
    }

    private static String generateReference(String prefix) {
        byte[] randomBytes = new byte[REFERENCE_LENGTH];
        random.nextBytes(randomBytes);
        String randomPart = Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
        return String.format("%s-%s", prefix, randomPart);
    }
}

