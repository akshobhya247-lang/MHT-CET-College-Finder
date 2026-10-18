
package com.cet.finder.service;

import com.cet.finder.exception.AuthException;
import com.cet.finder.util.PasswordUtil;

public class AuthService {

    public AuthService() {
        // Authentication service
    }

    public boolean verifyPassword(String rawPassword, String storedHash)
            throws AuthException {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new AuthException("Password cannot be empty.");
        }

        if (storedHash == null || storedHash.isBlank()) {
            throw new AuthException("Stored password hash is missing.");
        }

        try {
            return PasswordUtil.verifyPassword(rawPassword, storedHash);
        } catch (RuntimeException e) {
            throw new AuthException("Password verification failed.", e);
        }
    }
}
