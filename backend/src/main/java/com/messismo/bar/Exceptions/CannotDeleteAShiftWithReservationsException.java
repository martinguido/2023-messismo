package com.messismo.bar.Exceptions;

public class CannotDeleteAShiftWithReservationsException extends Exception{
    public CannotDeleteAShiftWithReservationsException() {
        super();
    }

    public CannotDeleteAShiftWithReservationsException(String message) {
        super(message);
    }

    public CannotDeleteAShiftWithReservationsException(String message, Throwable cause) {
        super(message, cause);
    }
}
