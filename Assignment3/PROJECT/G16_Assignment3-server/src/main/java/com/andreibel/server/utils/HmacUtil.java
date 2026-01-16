package com.andreibel.server.utils;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * Utility class for HMAC-SHA256 cryptographic operations.
 * <p>
 * Provides methods for computing HMAC-SHA256 hashes and performing
 * constant-time string comparisons to prevent timing attacks.
 * </p>
 *
 * @author Andrei Beloziyorove
 */
public final class HmacUtil {
    private HmacUtil() {}

    /**
     * Computes the HMAC-SHA256 hash of a message using the provided secret key.
     *
     * @param message the byte array message to hash
     * @param secret  the secret key used for HMAC computation
     * @return the hexadecimal string representation of the HMAC-SHA256 hash
     * @throws RuntimeException if HMAC computation fails
     */
    public static String hmacSha256Hex(byte[] message, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(keySpec);
            byte[] raw = mac.doFinal(message);
            return toHex(raw);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute HMAC-SHA256", e);
        }
    }

    /**
     * Compares two hexadecimal strings in constant time to prevent timing attacks.
     * <p>
     * Uses {@link MessageDigest#isEqual(byte[], byte[])} for secure comparison.
     * </p>
     *
     * @param a first hexadecimal string to compare
     * @param b second hexadecimal string to compare
     * @return {@code true} if the strings are equal, {@code false} otherwise
     */
    public static boolean constantTimeEqualsHex(String a, String b) {
        if (a == null || b == null) return false;
        // constant-time compare on bytes
        return MessageDigest.isEqual(
                a.getBytes(StandardCharsets.US_ASCII),
                b.getBytes(StandardCharsets.US_ASCII)
        );
    }

    /**
     * Converts a byte array to its hexadecimal string representation.
     *
     * @param bytes the byte array to convert
     * @return the lowercase hexadecimal string representation
     */
    private static String toHex(byte[] bytes) {
        char[] out = new char[bytes.length * 2];
        final char[] digits = "0123456789abcdef".toCharArray();
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            out[i * 2] = digits[v >>> 4];
            out[i * 2 + 1] = digits[v & 0x0F];
        }
        return new String(out);
    }
}