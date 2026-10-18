package com.cet.finder.exception;


public class CetException extends Exception {

    public CetException(String message)
    {
        super(message);
    }


    public CetException(String message, Throwable cause)
    {
        super(message , cause) ;
    }
}