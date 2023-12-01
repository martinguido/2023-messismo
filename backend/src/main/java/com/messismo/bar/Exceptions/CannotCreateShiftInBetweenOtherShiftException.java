package com.messismo.bar.Exceptions;

public class CannotCreateShiftInBetweenOtherShiftException extends Exception{
    public CannotCreateShiftInBetweenOtherShiftException() {
        super();
    }

    public CannotCreateShiftInBetweenOtherShiftException(String message) {
        super(message);
    }

    public CannotCreateShiftInBetweenOtherShiftException(String message, Throwable cause) {
        super(message, cause);
    }
}
