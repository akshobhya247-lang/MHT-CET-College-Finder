
package com.cet.finder;

import com.cet.finder.util.PasswordUtil;

public class PasswordUtilTest {

    public static void main(String[] args) {
        String password = "TestPassword@123";

        String hash = PasswordUtil.hashPassword(password);

        System.out.println("Hash generated: " + !hash.isEmpty());
        System.out.println(
                "Correct password: " +
                        PasswordUtil.verifyPassword(password, hash)
        );
        System.out.println(
                "Wrong password: " +
                        PasswordUtil.verifyPassword("WrongPassword", hash)
        );
    }
}
