package com.messismo.bar.Exceptions;

public class ReservationNotFoundException extends Exception {
    public ReservationNotFoundException() {
        super();
    }

    public ReservationNotFoundException(String message) {
        super(message);
    }

    public ReservationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
