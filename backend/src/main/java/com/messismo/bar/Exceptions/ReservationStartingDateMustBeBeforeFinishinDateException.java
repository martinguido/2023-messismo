package com.messismo.bar.Exceptions;

public class ReservationStartingDateMustBeBeforeFinishinDateException extends Exception{
    public ReservationStartingDateMustBeBeforeFinishinDateException() {
        super();
    }

    public ReservationStartingDateMustBeBeforeFinishinDateException(String message) {
        super(message);
    }

    public ReservationStartingDateMustBeBeforeFinishinDateException(String message, Throwable cause) {
        super(message, cause);
    }
}
