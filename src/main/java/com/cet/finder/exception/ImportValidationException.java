package com.cet.finder.exception;

public class ImportValidationException extends CetException {

    public ImportValidationException(String message)
    {
        super(message);
    }

    public ImportValidationException(
            String message, Throwable cause)
        {
         super(message , cause);
        }

}