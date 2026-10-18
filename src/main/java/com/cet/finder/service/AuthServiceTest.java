
package com.cet.finder.service;

import com.cet.finder.util.PasswordUtil;
import com.cet.finder.exception.AuthException;

public class AuthServiceTest {

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        String password = "TestPassword@123";
        String hash = PasswordUtil.hashPassword(password);

        try {
            System.out.println(
                    "Correct password: "
                            + authService.verifyPassword(password, hash));

            System.out.println(
                    "Wrong password: "
                            + authService.verifyPassword("WrongPassword", hash));

            try {
                authService.verifyPassword("", hash);
                System.out.println("Empty password test FAILED");
            } catch (AuthException e) {
                System.out.println("Empty password rejected: true");
            }

        } catch (AuthException e) {
            System.out.println("Authentication test failed: " + e.getMessage());
        }
    }
}
