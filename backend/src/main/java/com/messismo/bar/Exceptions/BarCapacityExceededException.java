package com.messismo.bar.Exceptions;

public class BarCapacityExceededException extends Exception {
    public BarCapacityExceededException() {
        super();
    }

    public BarCapacityExceededException(String message) {
        super(message);
    }

    public BarCapacityExceededException(String message, Throwable cause) {
        super(message, cause);
    }
}
