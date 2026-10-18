
package com.cet.finder.util;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordUtil {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 210_000;
    private static final int KEY_LENGTH = 256;

    private PasswordUtil() {
        // Utility class; do not instantiate.
    }

    public static String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException(
                    "Password cannot be empty."
            );
        }

        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] hash = deriveKey(password, salt);

        return ITERATIONS + ":" +
                Base64.getEncoder().encodeToString(salt) + ":" +
                Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(
            String password, String storedHash) {

        if (password == null || storedHash == null) {
            return false;
        }

        try {
            String[] parts = storedHash.split(":", -1);

            if (parts.length != 3) {
                return false;
            }

            int iterations = Integer.parseInt(parts[0]);

            if (iterations < 1 || iterations > 1_000_000) {
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash =
                    Base64.getDecoder().decode(parts[2]);

            if (salt.length != SALT_LENGTH ||
                    expectedHash.length != KEY_LENGTH / 8) {
                return false;
            }

            byte[] actualHash =
                    deriveKey(password, salt, iterations);

            return java.security.MessageDigest.isEqual(
                    expectedHash, actualHash
            );

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] deriveKey(
            String password, byte[] salt) {
        return deriveKey(password, salt, ITERATIONS);
    }

    private static byte[] deriveKey(
            String password, byte[] salt, int iterations) {

        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_LENGTH
        );

        try {
            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(spec).getEncoded();

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Password hashing failed.", e
            );

        } finally {
            spec.clearPassword();
        }
    }
}
