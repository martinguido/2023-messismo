package com.messismo.bar.Exceptions;

public class BarNotFoundException extends Exception{
    public BarNotFoundException() {
        super();
    }

    public BarNotFoundException(String message) {
        super(message);
    }

    public BarNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
