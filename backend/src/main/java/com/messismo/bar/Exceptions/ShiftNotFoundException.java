package com.messismo.bar.Exceptions;

public class ShiftNotFoundException extends Exception{

    public ShiftNotFoundException() {
        super();
    }

    public ShiftNotFoundException(String message) {
        super(message);
    }

    public ShiftNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
