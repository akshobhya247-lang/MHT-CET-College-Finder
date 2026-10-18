package com.cet.finder.exception;

public class AuthException extends CetException {

    public AuthException(String message)
    {
        super(message);
    }


    public AuthException(String message, Throwable cause)
    {
        super(message, cause);
    }
}